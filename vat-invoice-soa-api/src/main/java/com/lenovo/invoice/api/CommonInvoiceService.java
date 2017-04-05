package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceService {

    public RemoteResult addCommonInvoice(String lenovoId,String invoiceTitle,Integer shopid,String createBy) throws Exception;

    public RemoteResult<CommonInvoice> getCommonInvoice(String lenovoId,Integer shopid) throws Exception;

    //public RemoteResult updateCommonInvoice(Integer id,String invoiceTitle);

}
