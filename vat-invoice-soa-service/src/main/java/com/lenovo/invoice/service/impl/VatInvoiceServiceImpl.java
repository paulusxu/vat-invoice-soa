package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.VatInvoiceMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.api.query.order.OrderDetailService;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.ordercenter.soa.domain.forward.Main;
import com.lenovo.m2.ordercenter.soa.domain.forward.DeliveryAddress;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mayan3 on 2017/2/13.
 */
@Service("vatInvoiceService")
public class VatInvoiceServiceImpl implements VatInvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.vatinvoice");

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;
    @Autowired
    private VatInvoiceMapper vatInvoiceMapper;

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
            if (remoteResultInvoice.isSuccess()) {
                Invoice invoice = remoteResultInvoice.getT();//发票类型1:电子票2:增票3:普票
                if (invoice != null && invoice.getType() == 2) {
                    vathrowBtcp.setIsneedmerge(invoice.getIsNeedMerge());
                    vathrowBtcp.setOrderCode(orderId+"");
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
                        String zid= invoice.getZid();
                        vathrowBtcp.setZid(zid);

                        VatInvoice vatInvoice = getVatInvoiceByZid(zid, shopid);
                        if (vatInvoice != null) {
                            vathrowBtcp.setTitle(vatInvoice.getCustomername());//发票抬头
                            vathrowBtcp.setTaxpayeridentity(vatInvoice.getTaxno());//税号
                            vathrowBtcp.setBankno(vatInvoice.getAccountno());//开户账号
                            vathrowBtcp.setDepositbank(vatInvoice.getBankname());//开户行
                            vathrowBtcp.setRegisteraddress(vatInvoice.getAddress());//注册地址
                            vathrowBtcp.setRegisterphone(vatInvoice.getPhoneno());//电话
                        }
                        int rows = vathrowBtcpMapper.insert(vathrowBtcp);
                        if (rows > 0) {
                            //更新增票状态
                            updateVatInvoiceIsvalid(zid, shopid + "");
                        }
                        LOGGER.info("VathrowBtcp:{},{}", JacksonUtil.toJson(vathrowBtcp), rows);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public VatInvoice getVatInvoiceByZid(String zid, String shopid) {
        return vatInvoiceMapper.getVatInvoiceByZid(zid, shopid);
    }

    @Override
    public void updateVatInvoiceIsvalid(String zid, String shopid) {
        vatInvoiceMapper.updateVatInvoiceIsvalid(zid, shopid);
    }

}