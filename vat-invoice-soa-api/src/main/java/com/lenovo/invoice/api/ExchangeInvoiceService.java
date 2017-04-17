package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

/**
 * Created by admin on 2017/3/19.
 */
public interface ExchangeInvoiceService {

    //校验是否可以换票
    public RemoteResult ifExchangeVatInvoice(String orderCode);

    //BTCP回调接口
    public RemoteResult BTCPCallback(String applyId,String code,String message);

    //换普票
    public RemoteResult exchangeToCommon(String orderCode, Integer shopid, String lenovoId,String itCode,Integer oldInvoiceType,
                                         Integer exchangeType,Integer type,String newInvoiceTitle);

    //换增票
    public RemoteResult exchangeToVat(String orderCode, Integer shopid, String lenovoId,String itCode,
                                      Integer oldInvoiceType,Integer exchangeType,String newInvoiceTitle,
                                      String newTaxNo,String newBankName,String newBankNo,String newAddress,
                                      String newPhone,String faid,String faType);

    //获取换票记录，加分页
    public RemoteResult<PageModel2<ExchangeInvoiceRecord>> getExchangeInvoiceRecordByPage(PageQuery pageQuery, ExchangeInvoiceRecord exchangeInvoiceRecord);

    //获取换票记录详情
    public RemoteResult<ExchangeInvoiceRecord> getExchangeInvoiceRecord(String id);

    /*public RemoteResult vatToVat(String orderCode,String itCode,Integer oldInvoiceId,String oldInvoiceTitle,
                                 AddVatInvoiceInfoParam addVatInvoiceInfoParam);*/

    /*public RemoteResult exchangeInvoice(String itCode,String lenovoId,String orderCode, Integer shopid,
                                        Integer oldInvoiceId,String oldInvoiceTitle,Integer oldInvoiceType,
                                        Integer newInvoiceId,String newInvoiceTitle,Integer newInvoiceType,
                                        String taxNo,String bankName,String bankNo,String address,String phone
                                        ,String faid,String faType);*/

    /*public RemoteResult vatToCommon(String orderCode,Integer shopid,String lenovoId,String invoiceTitle,
                                    String itCode, Integer oldInvoiceId,String oldInvoiceTitle);*/

}
