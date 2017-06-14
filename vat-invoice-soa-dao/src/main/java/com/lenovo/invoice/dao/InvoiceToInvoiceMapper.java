package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.InvoiceToInvoice;
import com.lenovo.invoice.domain.VatInvoice;

/**
 * Created by admin on 2017/6/14.
 */
public interface InvoiceToInvoiceMapper {

    //保存一条废弃发票到有效发票的映射
    public int saveInvoiceToInvoice(InvoiceToInvoice invoice);

    //查询废弃id对应的目标发票ID
    public InvoiceToInvoice getInvoiceToInvoice(Long id);

    //根据废弃id查询有效记录
    public VatInvoice getVatInvoice(Long id);

}
