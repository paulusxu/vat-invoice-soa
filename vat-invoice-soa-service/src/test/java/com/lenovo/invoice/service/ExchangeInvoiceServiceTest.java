package com.lenovo.invoice.service;

import com.lenovo.invoice.api.ExchangeInvoiceService;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by admin on 2017/9/4.
 */
public class ExchangeInvoiceServiceTest extends BaseServiceTest {

    @Autowired
    private ExchangeInvoiceService exchangeInvoiceService;

    @Test
    public void ifExchangeVatInvoiceTest(){
        String orderCode = "21321";
        RemoteResult remoteResult = exchangeInvoiceService.ifExchangeVatInvoice(orderCode);
        System.out.println(JacksonUtil.toJson(remoteResult));
    }

}
