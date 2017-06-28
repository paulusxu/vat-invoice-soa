package com.lenovo.invoice.service.message;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.common.utils.EmailUtil;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.invoice.dao.InvoiceOrderMapper;
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
 * 将已支付订单所使用的普票和电子票置为有效
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
                if ((shopId==1||shopId==3||shopId==14)&&(type==0||type==1)){
                    String invoiceId = jsonObject.getString("invoiceId");
                    RemoteResult<VatInvoice> remoteResult = commonInvoiceService.getInvoiceByIdForAll(Long.parseLong(invoiceId), new Tenant());
                    if (remoteResult.isSuccess()){
                        //能查到这张票，如果是无效，将其置为有效
                        VatInvoice invoice = remoteResult.getT();
                        if (invoice!=null && invoice.getIsvalid()==0){
                            int i = commonInvoiceMapper.updateInvoiceIsValid(Long.parseLong(invoiceId));
                            if (i==1){
                                LOGGER.info("普票和电子票置为有效成功=="+i);
                            }
                        }
                        String customername = invoice.getCustomername();
                        String taxno = invoice.getTaxno();
                        Integer invoiceType = invoice.getInvoiceType();
                        Integer custType = invoice.getCustType();
                        Integer taxNoType = invoice.getTaxNoType();

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
                            try {
                                InvoiceOrder invoiceOrder = new InvoiceOrder();
                                invoiceOrder.setInvoiceId(Long.parseLong(invoiceId));
                                invoiceOrder.setOrderCode(orderCode);
                                invoiceOrder.setInvoiceTitle(customername);
                                invoiceOrder.setInvoiceType(invoiceType);
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

                                int i = invoiceOrderMapper.addInvoiceOrder(invoiceOrder);
                                if (i==1){
                                    LOGGER.info("添加发票和订单的映射表记录成功=="+i);
                                }
                            }catch (Exception e){
                                LOGGER.info("添加发票和订单的映射表记录出现异常=="+e.getMessage(),e);
                            }
                            //置为有效后，判断是否是需要审核的，且未审核的，如果是，发邮件通知客服
                            try {
                                Integer ischeck = invoice.getIscheck();
                                if (invoice!=null && ischeck==0 && custType==1 && taxNoType!=3){
                                    //未审核，发邮件
                                    //拼邮件1.下单账号，2.发票抬头，3.识别码类型，4.税号，5.发票类型，6.订单号，7.收货人，8.收货电话。税务登记证（15位）统一社会信用代码（18位）
                                    String taxNoTypeStr;
                                    if (taxNoType==1){
                                        taxNoTypeStr = "税务登记证（15位）";
                                    }else {
                                        taxNoTypeStr = "统一社会信用代码（18位）";
                                    }
                                    String typeStr;
                                    if (type==0){
                                        typeStr = "电子票";
                                    }else {
                                        typeStr = "普通发票";
                                    }
                                    String content = "您好，有待审核发票请您尽快去审核，信息如下：下单账号:"+memberCode+";发票抬头:"+invoice.getCustomername()
                                            +";识别码类型:"+taxNoTypeStr+";税号:"+invoice.getTaxno()+";发票类型:"+typeStr+";订单号:"+orderCode
                                            +";收货人:"+name+";收货电话:"+mobile+"。";
                                    String title = "发票审核";
                                    emailUtil.sendEmail(title,content);
                                }
                            }catch (Exception e){
                                LOGGER.info("审核发票发邮件出现异常=="+e.getMessage(),e);
                            }
                        }
                    }
                }
            }catch (Exception e){
                LOGGER.error("普票和电子票置为有效出现异常=="+message+"=="+e.getMessage(),e);
            }
        }
    }
}
