package com.lenovo.invoice.service;

import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;

import java.util.List;
import java.util.Map;

/**
 * Created by mayan3 on 2017/2/13.
 */
public interface VatInvoiceService {
    void parseInvoice(String orderCode);

    VatInvoice getVatInvoiceByZid(String zid, String shopid);

    //抛单后修改增票 为有效
    void updateVatInvoiceIsvalid(String zid, String shopid);
    //抛增票到btcp
    void throwBTCP(List<VathrowBtcp> btcpList);
}
