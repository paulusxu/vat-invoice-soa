package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.OrderInvoice;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

/**
 * Created by mayan3 on 2017/6/7.
 */
public interface OrderInvoiceApiService {
    RemoteResult<Boolean> initOrderInvoice(OrderInvoice orderInvoice);
}
