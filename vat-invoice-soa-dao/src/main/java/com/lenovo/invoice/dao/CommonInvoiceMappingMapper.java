package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.CommonInvoiceMapping;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceMappingMapper {

    public int addCommonInvoiceMapping(CommonInvoiceMapping commonInvoiceMapping);

    public CommonInvoiceMapping getCommonInvoiceMapping(CommonInvoiceMapping commonInvoiceMapping);

}
