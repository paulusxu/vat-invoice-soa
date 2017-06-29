package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.InvoiceOrder;

import java.util.List;

/**
 * Created by admin on 2017/6/27.
 */
public interface InvoiceOrderMapper {

    public int addInvoiceOrder(InvoiceOrder invoiceOrder);

    public int updateOrderStatus(Long orderCode);

    public List<InvoiceOrder> getOrderIds(Long invoiceId);

    public int deleteInvalid();

    public int deleteInvalidRollback(Long id);

}
