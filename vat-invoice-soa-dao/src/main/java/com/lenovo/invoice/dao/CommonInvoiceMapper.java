package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.CommonInvoice;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceMapper {

    public int addCommonInvoice(CommonInvoice commonInvoice);

    public CommonInvoice getCommonInvoiceById(Integer id);

    public CommonInvoice getCommonInvoiceByTitle(CommonInvoice commonInvoice);

    public int updateCommonInvoice(CommonInvoice commonInvoice);

}
