package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceService {

    public RemoteResult addCommonInvoice(CommonInvoice commonInvoice) throws Exception;

    public RemoteResult<CommonInvoice> getCommonInvoice(CommonInvoice commonInvoice);

    public RemoteResult<CommonInvoice> getCommonInvoiceByIds(String lenovoId,Integer id,Tenant tenant);

    //public RemoteResult updateCommonInvoice(Integer id,String invoiceTitle);

}
