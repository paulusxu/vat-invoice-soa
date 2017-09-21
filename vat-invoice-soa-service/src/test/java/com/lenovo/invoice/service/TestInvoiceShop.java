package com.lenovo.invoice.service;

import com.lenovo.invoice.api.InvoiceShopApiService;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.domain.InvoiceIdAndUuid;
import com.lenovo.invoice.domain.InvoiceShop;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by xuweihua on 2017/9/15.
 */
public class TestInvoiceShop extends BaseServiceTest {
    @Autowired
    private InvoiceShopApiService invoiceShopApiService;

    @Test
    public void sys(){
        InvoiceShop invoiceShop=new InvoiceShop();
        try {
            invoiceShop.setCustomerName("测试发票公司");
            invoiceShop.setLenovoID("154463");
            invoiceShop.setInvoiceType(1);
            invoiceShop.setPayManType(1);
            invoiceShop.setPayMan("测试发票公司");
            invoiceShop.setTaxNo("273489875989687586");
            invoiceShop.setTaxNoType("1");
            invoiceShop.setCompanyType(1);
            invoiceShop.setBankName("银行");
            invoiceShop.setAccountNo("3342342323");
            invoiceShop.setSubAreaName("海淀");
            invoiceShop.setZip("100000");
            invoiceShop.setProvinceName("北京");
            invoiceShop.setProvinceCode("北京");
            invoiceShop.setCityName("北京");
            invoiceShop.setCityCode("北京");
            invoiceShop.setCountyName("XXX县");
            invoiceShop.setCountyCode("XXX县");
            invoiceShop.setAddress("MXMXMXM地址");
            invoiceShop.setPhoneNo("手机号");
            invoiceShop.setIsDefault(0);
            invoiceShop.setSynType(1);
            invoiceShop.setShopId(8);
            RemoteResult<InvoiceIdAndUuid> remoteResult= invoiceShopApiService.synInvoice(invoiceShop);
            System.out.println(JacksonUtil.toJson(remoteResult));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void get(){
        RemoteResult<List<InvoiceShop>> remoteResult= invoiceShopApiService.queryInvoice("209515");
        System.out.println(JacksonUtil.toJson(remoteResult));
    }
}
