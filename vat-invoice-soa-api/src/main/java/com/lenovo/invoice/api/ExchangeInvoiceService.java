package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

/**
 * Created by admin on 2017/3/19.
 */
public interface ExchangeInvoiceService {

    public RemoteResult ifExchangeVatInvoice(String orderCode);

    public RemoteResult exchangeInvoice(String itCode,String lenovoId,String orderCode, Integer shopid,
                                        Integer oldInvoiceId,String oldInvoiceTitle,Integer oldInvoiceType,
                                        Integer newInvoiceId,String newInvoiceTitle,Integer newInvoiceType,
                                        String taxNo,String bankName,String bankNo,String address,String phone
                                        ,String faid,String faType);

    public RemoteResult commonToCommon(String orderCode,Integer shopid,String lenovoId,String invoiceTitle,
                                       String itCode,Integer oldInvoiceId,String oldInvoiceTitle);

    public RemoteResult commonToVat(String orderCode,String itCode,Integer oldInvoiceId,String oldInvoiceTitle,
                                    AddVatInvoiceInfoParam addVatInvoiceInfoParam);

    public RemoteResult vatToVat(String orderCode,String itCode,Integer oldInvoiceId,String oldInvoiceTitle,
                                 AddVatInvoiceInfoParam addVatInvoiceInfoParam);

    public RemoteResult vatToCommon(String orderCode,Integer shopid,String lenovoId,String invoiceTitle,
                                    String itCode, Integer oldInvoiceId,String oldInvoiceTitle);

}
