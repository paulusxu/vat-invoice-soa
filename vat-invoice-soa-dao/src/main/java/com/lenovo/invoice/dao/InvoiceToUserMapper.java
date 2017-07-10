package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.InvoiceToUser;
import com.lenovo.invoice.domain.VatInvoice;

import java.util.List;

/**
 * Created by admin on 2017/7/6.
 */
public interface InvoiceToUserMapper {

    //添加记录
    public int addInvoiceToUser(InvoiceToUser invoiceToUser);

    //查询是否有相同的记录
    public List<InvoiceToUser> ifExistsSameRecord(InvoiceToUser invoiceToUser);

    //查询该用户使用过的所有已审核公司普票和电票Lenovo，epp
    public List<VatInvoice> getInvoiceByUser(InvoiceToUser invoiceToUser);


}
