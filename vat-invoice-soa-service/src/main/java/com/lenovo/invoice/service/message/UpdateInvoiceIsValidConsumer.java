package com.lenovo.invoice.service.message;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.common.utils.AutoCheckInvoiceUtil;
import com.lenovo.invoice.common.utils.EmailUtil;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.ChangeInvoiceHistoryMapper;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.invoice.dao.InvoiceOrderMapper;
import com.lenovo.invoice.dao.VatInvoiceMapper;
import com.lenovo.invoice.domain.ChangeInvoiceHistory;
import com.lenovo.invoice.domain.InvoiceOrder;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import com.lenovo.m2.ordercenter.soa.api.query.order.OrderDetailService;
import com.lenovo.m2.ordercenter.soa.domain.forward.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by admin on 2017/6/14.
 * 将已支付订单所使用的普票和电子票置为有效，并判断是否已审核，如果为未审核，则调用工具类去审核，审核失败则发邮件通知业务审核。
 */
public class UpdateInvoiceIsValidConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateInvoiceIsValidConsumer.class);

    private KafkaConsumer kafkaConsumer;
    @Autowired
    private CommonInvoiceMapper commonInvoiceMapper;
    @Autowired
    private CommonInvoiceService commonInvoiceService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private InvoiceOrderMapper invoiceOrderMapper;
    @Autowired
    private VatInvoiceMapper vatInvoiceMapper;
    @Autowired
    private ChangeInvoiceHistoryMapper changeInvoiceHistoryMapper;

    public UpdateInvoiceIsValidConsumer(KafkaConsumer kafkaConsumer){
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String message) {
            LOGGER.info("订单支付后=普票和电子票置为有效消息=="+message);
            /*{"invoiceId":"String","orderCode":Long,"shopId":int,"type":int}0电子票1普票2增票*/
            try {
                JSONObject jsonObject = JSONObject.parseObject(message);
                Integer shopId = jsonObject.getInteger("shopId");
                 Integer type = jsonObject.getInteger("type");
                //只关注Lenovo和epp，发票为普票和电子票
                if ((shopId==1||shopId==3)&&(type==0||type==1)){
                    String invoiceId = jsonObject.getString("invoiceId");
                    RemoteResult<VatInvoice> remoteResult = commonInvoiceService.getInvoiceByIdForAll(Long.parseLong(invoiceId), new Tenant());
                    if (remoteResult.isSuccess()){
                        //能查到这张票，如果是无效，将其置为有效
                        VatInvoice invoice = remoteResult.getT();
                        if (invoice==null){
                            //没查到发票，忽略
                            LOGGER.error("普票和电子票置为有效结束==未查询到发票=="+invoiceId);
                            return;
                        }
                        if (invoice.getCustType()==0){
                            //个人发票，忽略
                            LOGGER.error("普票和电子票置为有效结束==个人发票，不处理=="+invoiceId);
                            return;
                        }
                        if (invoice.getIsvalid()==0){
                            int i = commonInvoiceMapper.updateInvoiceIsValid(Long.parseLong(invoiceId));
                            if (i==1){
                                LOGGER.info("普票和电子票置为有效成功=="+i);
                            }
                        }

                        String customername = invoice.getCustomername();
                        String taxno = invoice.getTaxno();
                        Integer custType = invoice.getCustType();
                        Integer taxNoType = invoice.getTaxNoType();
                        Integer ischeck = invoice.getIscheck();

                        Long orderCode = jsonObject.getLong("orderCode");

                        LOGGER.info("支付后，查询订单信息参数=="+orderCode);
                        RemoteResult<Main> orderInfoByOrderId = orderDetailService.getOrderInfoByOrderId(orderCode);
                        LOGGER.info("支付后，查询订单信息返回值=="+ JacksonUtil.toJson(orderInfoByOrderId));
                        if (orderInfoByOrderId.isSuccess()){
                            Main ma = orderInfoByOrderId.getT();
                            String memberCode = ma.getMemberCode();
                            String name = ma.getDeliveryAddress().getName();
                            String mobile = ma.getDeliveryAddress().getMobile();
                            String address = ma.getDeliveryAddress().getAddress();
                            String zip = ma.getDeliveryAddress().getZip();
                            //添加发票和订单的映射表记录
                            addInvoiceToOrder(shopId, type, invoiceId, customername, taxno, custType, taxNoType, orderCode, memberCode, name, mobile, address, zip);

                            //判断这张票是否已审核过，如果未审核，则调用工具类去审核，审核成功修改审核状态，审核失败发邮件通知业务手动处理
                            if (ischeck==0){
                                //发票未审核，调用工具类审核
                                String autoTaxNo = AutoCheckInvoiceUtil.getTaxNo(customername);
                                if (Strings.isNullOrEmpty(autoTaxNo)) {
                                    //自动审核失败
                                    invoice.setCheckBy("admin_check");
                                    invoice.setIscheck(4);
                                    long rows = vatInvoiceMapper.updateVatInvoiceAutoCheck(invoice);
                                    if (rows > 0) {
                                        //未自动审核成功，发邮件
                                        sendCheckInvoiceEmail(type, customername, taxno, taxNoType, orderCode, memberCode, name, mobile);
                                    }
                                } else {
                                    if (!autoTaxNo.equals(taxno)) {
                                        //设置history表
                                        changeInvoiceHistoryMapper.insertChangeInvoiceHistory(new ChangeInvoiceHistory(invoice.getId(), customername, taxno, autoTaxNo));
                                        invoice.setTaxno(autoTaxNo);
                                        int len = autoTaxNo.length();
                                        //识别码类型，1是15、20位，2是18位，3是无
                                        invoice.setTaxNoType(len == 15 || len == 20 ? 1 : 2);
                                    }
                                    invoice.setCheckBy("admin_check");
                                    invoice.setIscheck(1);
                                    long rows = vatInvoiceMapper.updateVatInvoiceAutoCheck(invoice);
                                    if (rows > 0) {
                                        //审核成功，将相同抬头的其他未审核发票废弃，添加映射
                                        commonInvoiceService.deleteTheSameTitleInvoice(customername, invoice.getId());
                                    }
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                LOGGER.error("普票和电子票置为有效出现异常=="+message+"=="+e.getMessage(),e);
            }
        }
    }

    /**
     * 未自动审核成功，发邮件
     * @param type 发票类型
     * @param customername 发票抬头
     * @param taxno 发票税号
     * @param taxNoType 识别码类型
     * @param orderCode 订单号
     * @param memberCode 下单账号
     * @param name 收货人姓名
     * @param mobile 收货人电话
     */
    private void sendCheckInvoiceEmail(Integer type, String customername, String taxno, Integer taxNoType, Long orderCode, String memberCode, String name, String mobile) {
        try {
            //拼邮件1.下单账号，2.发票抬头，3.识别码类型，4.税号，5.发票类型，6.订单号，7.收货人，8.收货电话。税务登记证（15位）统一社会信用代码（18位）
            String taxNoTypeStr;
            if (taxNoType==1){
                taxNoTypeStr = "税务登记证（15位）";
            }else if (taxNoType==3){
                taxNoTypeStr = "无（政府机构，事业单位，非企业单位）";
            }else {
                taxNoTypeStr = "统一社会信用代码（18位）";
            }
            String typeStr;
            if (type==0){
                typeStr = "电子票";
            }else {
                typeStr = "普通发票";
            }
            String content = "您好，有待审核发票请您尽快去审核，信息如下：下单账号:"+memberCode+";发票抬头:"+customername
                    +";识别码类型:"+taxNoTypeStr+";税号:"+taxno+";发票类型:"+typeStr+";订单号:"+orderCode
                    +";收货人:"+name+";收货电话:"+mobile+"。";
            String title = "发票审核";
            emailUtil.sendEmail(title,content);
        }catch (Exception e){
            LOGGER.info("审核发票发邮件出现异常=="+e.getMessage(),e);
        }
    }

    /**
     * 添加发票和订单的映射表记录
     * @param shopId 商城
     * @param type 发票类型
     * @param invoiceId 发票id
     * @param customername 发票抬头
     * @param taxno 发票税号
     * @param custType 开票方式
     * @param taxNoType 识别码类型
     * @param orderCode 订单号
     * @param memberCode 下单账号
     * @param name 收货人姓名
     * @param mobile 收货人电话
     * @param address 收货人地址
     * @param zip 邮编
     */
    private void addInvoiceToOrder(Integer shopId, Integer type, String invoiceId, String customername, String taxno, Integer custType, Integer taxNoType, Long orderCode, String memberCode, String name, String mobile, String address, String zip) {
        try {
            //只保存公司的记录
            InvoiceOrder invoiceOrder = new InvoiceOrder();
            invoiceOrder.setInvoiceId(Long.parseLong(invoiceId));
            invoiceOrder.setOrderCode(orderCode);
            invoiceOrder.setInvoiceTitle(customername);
            if (type==0){
                invoiceOrder.setInvoiceType(1);
            }else {
                invoiceOrder.setInvoiceType(3);
            }
            invoiceOrder.setTaxNo(taxno);
            invoiceOrder.setTaxNoType(taxNoType);
            invoiceOrder.setCustType(custType);
            invoiceOrder.setMemberCode(memberCode);
            invoiceOrder.setName(name);
            invoiceOrder.setMobile(mobile);
            invoiceOrder.setAddress(address);
            invoiceOrder.setZip(zip);
            invoiceOrder.setCreateBy("admin");
            invoiceOrder.setUpdateBy("admin");
            invoiceOrder.setShopid(shopId);
            invoiceOrder.setOrderStatus(0);

            int i = invoiceOrderMapper.addInvoiceOrder(invoiceOrder);
            if (i==1){
                LOGGER.info("添加发票和订单的映射表记录成功=="+i);
            }
        }catch (Exception e){
            LOGGER.info("添加发票和订单的映射表记录出现异常=="+e.getMessage(),e);
        }
    }
}
