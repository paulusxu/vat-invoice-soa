package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.Consignee;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

/**
 * 收票地址
 * Created by mayan3 on 2017/5/15.
 */
public interface ConsigneeApiService {
    //更新收票地址
    RemoteResult<Boolean> updateConsignee(Consignee consignee);
}
