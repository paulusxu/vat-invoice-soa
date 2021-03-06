package com.lenovo.invoice.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.lenovo.invoice.common.utils.*;
import com.lenovo.invoice.dao.VatInvoiceMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.api.query.order.OrderDetailService;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import com.lenovo.m2.ordercenter.soa.domain.forward.DeliveryAddress;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.ordercenter.soa.domain.forward.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mayan3 on 2017/2/13.
 */
@Service("vatInvoiceService")
public class VatInvoiceServiceImpl implements VatInvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.vatinvoice");
    private static final Logger LOGGER_BTCP = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.throwBtcp");
    private static final Logger LOGGER_THROW = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.throw");
    private static final Logger LOGGER_PAID = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.paid");


    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;
    @Autowired
    private VatInvoiceMapper vatInvoiceMapper;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;

    @Override
    public void parseInvoice(String orderCode) {
        RemoteResult<Invoice> remoteResultInvoice = null;
        RemoteResult<Main> remoteResultMain = null;
        RemoteResult<DeliveryAddress> remoteResultDeliveryAddress = null;

        VathrowBtcp vathrowBtcp = new VathrowBtcp();
        try {
            long orderId = Long.parseLong(orderCode);
            //查询主单信息  获取发票类型
            remoteResultInvoice = orderDetailService.getInvoiceByOrderId(orderId);
            LOGGER_THROW.info("remoteResultInvoice:", JacksonUtil.toJson(remoteResultInvoice));
            if (remoteResultInvoice.isSuccess()) {
                Invoice invoice = remoteResultInvoice.getT();//发票类型1:电子票2:增票3:普票
                if (invoice != null && invoice.getType() == 2) {
                    LOGGER_THROW.info("invoice:", JacksonUtil.toJson(invoice));

                    vathrowBtcp.setIsneedmerge(invoice.getIsNeedMerge());
                    vathrowBtcp.setOrderCode(orderId + "");
                    //获取订单相关信息
                    remoteResultMain = orderDetailService.getMainById(orderId);
                    remoteResultDeliveryAddress = orderDetailService.getDeliveryAddressByOrderId(orderId, 1);//1代表是发票地址，0代表是货的地址

                    if (remoteResultMain.isSuccess() && remoteResultDeliveryAddress.isSuccess()) {
                        Main main = remoteResultMain.getT();
                        if (main != null) {
                            vathrowBtcp.setOutid(main.getOutId());
                            vathrowBtcp.setMembercode(main.getMemberCode());
                        }
                        DeliveryAddress deliveryAddress = remoteResultDeliveryAddress.getT();
                        LOGGER_THROW.info("deliveryAddress:", JacksonUtil.toJson(deliveryAddress));
                        if (deliveryAddress != null) {
                            //设置收货信息
                            vathrowBtcp.setName(deliveryAddress.getName());//收货人姓名
                            vathrowBtcp.setProvinceid(deliveryAddress.getProvinceId());//省份编号
                            vathrowBtcp.setCity(deliveryAddress.getCity());//市名称
                            vathrowBtcp.setCounty(deliveryAddress.getCounty());//区县名称
                            vathrowBtcp.setAddress(deliveryAddress.getAddress());//详细地址
                            vathrowBtcp.setPhone(deliveryAddress.getPhone());//联系电话
                            vathrowBtcp.setZip(deliveryAddress.getZip());//邮编
                        }
                        //设置增票信息
                        String shopid = invoice.getTenant().getShopId() + "";
                        String zid = invoice.getZid();
                        vathrowBtcp.setZid(zid);
                        vathrowBtcp.setOrderStatus(2);
                        vathrowBtcp.setPaidTime(main.getPaidTime());

                        VatInvoice vatInvoice = getVatInvoiceByZid(zid, shopid);
                        if (vatInvoice != null) {
                            String customername = vatInvoice.getCustomername();
                            String taxno = vatInvoice.getTaxno();
                            if (vatInvoice.getIscheck() == 0) {
                                //自动校验抬头和税号
                                String autoTaxNo = AutoCheckInvoiceUtil.getTaxNo(customername);
                                if (Strings.isNullOrEmpty(autoTaxNo) || !autoTaxNo.equals(taxno)) {
                                    //自动审核失败 需要人工审核
                                    vatInvoice.setCheckBy("admin_check");
                                    vatInvoice.setIscheck(4);
                                }
                            }

                            vathrowBtcp.setTitle(customername);//发票抬头
                            vathrowBtcp.setTaxpayeridentity(taxno);//税号
                            vathrowBtcp.setBankno(vatInvoice.getAccountno());//开户账号
                            vathrowBtcp.setDepositbank(vatInvoice.getBankname());//开户行
                            vathrowBtcp.setRegisteraddress(vatInvoice.getAddress());//注册地址
                            vathrowBtcp.setRegisterphone(vatInvoice.getPhoneno());//电话
                        }
                        int rows = vathrowBtcpMapper.updateVathrowbtcp(vathrowBtcp);
                        if (rows > 0) {
                            //更新增票状态
                            updateVatInvoiceIsvalid(zid, shopid + "");
                        }
                        LOGGER_THROW.info("VathrowBtcp:{},{}", JacksonUtil.toJson(vathrowBtcp), rows);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER_THROW.error(e.getMessage(), e);
        }
    }

    @Override
    public long initVathrowBtcp(String orderCode, String zid, int shopId) {
        long rows = 0;
        LOGGER_PAID.info("InitVathrowBtcp:orderCode:{},zid:{},shopId:{}", orderCode, zid, shopId);
        try {
            if (shopId != 8 && shopId != 14 && shopId != 15) {
                VathrowBtcp vathrowBtcp = new VathrowBtcp();
                vathrowBtcp.setOrderStatus(1);
                vathrowBtcp.setOrderCode(orderCode);
                vathrowBtcp.setZid(zid);
                //初始化
                rows = vathrowBtcpMapper.insertVathrowBtcp(vathrowBtcp);
                //自动审核增票
                VatInvoice vatInvoice = getVatInvoiceByZid(zid, shopId + "");
                if (vatInvoice != null && vatInvoice.getIscheck() == 0) {
                    String customername = vatInvoice.getCustomername();
                    String taxNo = vatInvoice.getTaxno();
                    String autoTaxNo = AutoCheckInvoiceUtil.getTaxNo(customername);
                    if (Strings.isNullOrEmpty(autoTaxNo)) {
                        //自动审核失败
                        vatInvoice.setCheckBy("admin_check");
                        vatInvoice.setIscheck(4);
                        vatInvoiceMapper.updateVatInvoiceAutoCheck(vatInvoice);
                    } else {
                        if (!autoTaxNo.equals(taxNo)) {
                            vatInvoice.setCheckBy("admin_check");
                            vatInvoice.setIscheck(4);
                        } else {
                            vatInvoice.setCheckBy("admin_check");
                            vatInvoice.setIscheck(1);
                        }
                        vatInvoiceMapper.updateVatInvoiceAutoCheck(vatInvoice);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER_PAID.error(e.getMessage());
        }
        return rows;
    }

    @Override
    public VatInvoice getVatInvoiceByZid(String zid, String shopid) {
        return vatInvoiceMapper.getVatInvoiceByZid(zid, shopid);
    }

    @Override
    public void updateVatInvoiceIsvalid(String zid, String shopid) {
        vatInvoiceMapper.updateVatInvoiceIsvalid(zid, shopid);
    }

    @Override
    public void throwBTCP(List<VathrowBtcp> btcpList) {
        try {
            for (VathrowBtcp vathrowBtcp : btcpList) {
                LOGGER_BTCP.info("开始准备抛单orderId=[" + vathrowBtcp.getOrderCode() + "]");
                Map<String, String> map = Maps.newHashMap();
                String xml = "";
                String context = "<CUSTOMERNAME>" + vathrowBtcp.getTitle() + "</CUSTOMERNAME>";
                context += "<TAXNO>" + vathrowBtcp.getTaxpayeridentity() + "</TAXNO>";
                context += "<BANKNAME>" + vathrowBtcp.getDepositbank() + "</BANKNAME>";
                context += "<ACCOUNTNO>" + vathrowBtcp.getBankno() + "</ACCOUNTNO>";
                context += "<ADDRESS>" + vathrowBtcp.getRegisteraddress() + "</ADDRESS>";
                context += "<PHONENO>" + vathrowBtcp.getRegisterphone() + "</PHONENO>";
                context += "<BTCPSO>" + vathrowBtcp.getOutid() + "</BTCPSO>";
                context += "<TAKERNAME>" + vathrowBtcp.getName() + "</TAKERNAME>";

                context += "<TAKERAREA>" + vathrowBtcp.getProvinceid() + "</TAKERAREA>";
                context += "<TAKERCITY>" + vathrowBtcp.getCity() + "</TAKERCITY>";
                context += "<TAKERCOUNTY>" + vathrowBtcp.getCounty() + "</TAKERCOUNTY>";

                context += "<TAKERADDRESS>" + vathrowBtcp.getAddress() + "</TAKERADDRESS>";
                context += "<TAKERPHONE>" + vathrowBtcp.getPhone() + "</TAKERPHONE>";
                context += "<TAKERPOST>" + vathrowBtcp.getZip() + "</TAKERPOST>";
                context += "<ISMERGEINVOICE>" + vathrowBtcp.getIsneedmerge() + "</ISMERGEINVOICE>";
                context += "<PURCHASEID>" + vathrowBtcp.getMembercode() + "</PURCHASEID>";
                xml = "<VATTAX>" + context + "</VATTAX>";
                LOGGER_BTCP.info("POST to BTCP  XML = " + xml);
                String data_digest = MD5.sign(xml, propertiesUtil.getKey(), "utf-8");
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("xml", xml);
                paramMap.put("cid", "officialportal");
                paramMap.put("data_digest", data_digest);
                NetWorkWrapperUtil net = new NetWorkWrapperUtil();
                try {
                    String resposeData = net.requestData(propertiesUtil.getUrl(), paramMap);
                    map = XMLUtil.parseXml(resposeData);
                    LOGGER_BTCP.info("BTCP to POST result = [" + resposeData + "], orderCode=[" + vathrowBtcp.getId() + "], map=[" + map + "]");

                    String resCode = map.get("Code");
                    String message = map.get("Message");
                    String BTCPResultCode = map.get("ExcuteResult");

                    if (!resCode.equals("") && resCode.equals("200")) {
                        //抛送成功
                        int rows = vathrowBtcpMapper.updateByOrderCode(vathrowBtcp.getOrderCode(), 2, resCode);
                        if (rows > 0) {
                            //增票抛单成功通知订单
                            Invoice invoice = new Invoice();
                            invoice.setZCode(BTCPResultCode);

                            invoice.setTitle(vathrowBtcp.getTitle());
                            invoice.setTaxpayerIdentity(vathrowBtcp.getTaxpayeridentity());
                            invoice.setDepositBank(vathrowBtcp.getDepositbank());
                            invoice.setBankNo(vathrowBtcp.getBankno());
                            invoice.setRegisterAddress(vathrowBtcp.getRegisteraddress());
                            invoice.setRegisterPhone(vathrowBtcp.getRegisterphone());
                            invoice.setOrderId(Long.parseLong(vathrowBtcp.getOrderCode()));
                            invoice.setThrowStatus(2);
                            invoice.setType(2);

                            vatApiOrderCenter.updateInvoice(invoice);
                        }
                    } else {
                        //<Result><Code>213_700_020</Code><Message>213_700_020:只有审批被拒绝的情况下才可二次抛送</Message></Result>
                        int code = 4;
                        if (resCode.equals("213_700_017") || resCode.equals("9999")) {
                            //213_700_017 正向订单还未同步，请稍后在抛
                            //9999接口调用次数超限制的报错为统一报错
                            code = 0;
                        }
                        int rows = vathrowBtcpMapper.updateByOrderCode(vathrowBtcp.getOrderCode(), code, message);
                    }

                } catch (Exception e) {
                    LOGGER_BTCP.error("抛送增票失败，xml:" + xml + "   失败原因：" + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public int updateOrderStatus(String orderCode, int status) {
        int rows = 0;
        try {
            rows = vathrowBtcpMapper.updateOrderStatus(orderCode, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return rows;
    }

    @Override
    public int updateThrowingStatus(String orderCode, int status) {
        int rows = 0;
        try {
            rows = vathrowBtcpMapper.updateThrowingStatus(orderCode, status);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return rows;
    }

    @Override
    public List<VathrowBtcp> getThrowBtcpList() {
        List<VathrowBtcp> btcpList = null;
        try {
            btcpList = vathrowBtcpMapper.getThrowBtcpList();
        } catch (Exception e) {
            LOGGER_BTCP.error(e.getMessage(), e);
        }
        return btcpList;
    }

    @Override
    public long updateZid(String zid, String zids) {
        long rows = 0;
        try {
            VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(Long.parseLong(zid));
            vatInvoice.setFaid(zids);
            rows = vathrowBtcpMapper.updateZid(vatInvoice);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return rows;
    }

}
