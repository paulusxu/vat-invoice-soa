package com.lenovo.invoice.service;

import com.lenovo.invoice.domain.VatInvoice;

/**
 * Created by mayan3 on 2017/2/13.
 */
public interface VatInvoiceService {
    void parseInvoice(String orderCode);

    VatInvoice getVatInvoiceByZid(String zid, String shopid);

    //抛单后修改增票 为有效
    void updateVatInvoiceIsvalid(String zid, String shopid);
}
