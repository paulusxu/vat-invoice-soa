package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.invoice.domain.UpdateInvoiceInOrderParams;

import java.util.List;

/**
 * Created by admin on 2017/3/20.
 */
public interface ExchangeInvoiceRecordMapper {

    public int addExchangeInvoiceRecord(ExchangeInvoiceRecord exchangeInvoiceRecord);

    public ExchangeInvoiceRecord getExchangeInvoiceRecord(String id);

    public int updateExchangeInvoiceRecord(ExchangeInvoiceRecord exchangeInvoiceRecord);

    public int addErrorUpdateOrder(UpdateInvoiceInOrderParams updateInvoiceInOrderParams);

    public List<UpdateInvoiceInOrderParams> getErrorUpdateOrder();

    public int deleteErrorUpdateOrder(Integer id);

}
