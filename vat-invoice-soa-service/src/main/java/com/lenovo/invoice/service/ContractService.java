package com.lenovo.invoice.service;

import com.lenovo.invoice.domain.Contract;
import com.lenovo.invoice.domain.param.AddContractInfoParam;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

/**
 * Created by mayan3 on 2016/8/9.
 */
public interface ContractService {
    void getContractInfo(String orderCode,String userId,int shopId);
    long addContractInfo(AddContractInfoParam param);
    Contract getContractInfo(String lenovoId, String cId);
    long updateContractInfo(Contract contract);

}
