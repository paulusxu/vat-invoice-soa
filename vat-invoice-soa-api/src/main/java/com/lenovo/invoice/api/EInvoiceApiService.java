package com.lenovo.invoice.api;

import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.domain.BaseInfo;

/**
 * 电子票
 * Created by mayan3 on 2017/4/25.
 */
public interface EInvoiceApiService {
    //下载电子票
    public RemoteResult<BaseInfo> downLoadInvoice(String btcpCode, String itCode);
}
