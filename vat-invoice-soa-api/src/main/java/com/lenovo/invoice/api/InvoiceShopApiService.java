package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.InvoiceIdAndUuid;
import com.lenovo.invoice.domain.InvoiceShop;
import com.lenovo.invoice.domain.InvoiceShopModifyLog;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

import java.util.List;

/**
 * 17增票
 * Created by xuweihua on 2016/7/27.
 */
public interface InvoiceShopApiService {
    RemoteResult<InvoiceIdAndUuid> synInvoice(InvoiceShop invoiceShop)  throws Exception ;

    RemoteResult<List<InvoiceShop>> queryInvoice(String lenovoid);

    RemoteResult<InvoiceShop> queryInvoiceForId(String id,String lenovoid);

    RemoteResult<List<InvoiceShopModifyLog>> queryInvoiceLog(String count);

    RemoteResult<Integer> getIdByUUID(String uuid);

    RemoteResult modifyePersonalCenter(InvoiceShop invoiceShop) throws Exception;

    RemoteResult<InvoiceShop> queryInvoiceAuditForId(String id);

}
