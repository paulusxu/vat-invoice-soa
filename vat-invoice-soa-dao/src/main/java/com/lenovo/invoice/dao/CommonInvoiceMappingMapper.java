package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.CommonInvoice;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceMappingMapper {

    public int addCommonInvoiceMapping(CommonInvoice commonInvoice);

    public CommonInvoice getCommonInvoiceMapping(CommonInvoice commonInvoice);

}
