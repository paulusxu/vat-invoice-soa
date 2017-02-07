package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.Contract;
import com.lenovo.invoice.domain.param.AddContractInfoParam;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.my.domain.ordermessage.ordermessageforsmb.Pact;

import java.util.Map;

/**
 * 合同
 * Created by mayan3 on 2016/8/9.
 */
public interface ContractApiService {
    //获取合同信息
     RemoteResult<Contract> getContractInfo(String lenovoId, String cId);
    //分页查询合同
     PageModel2<Contract> getContractPage(PageQuery pageQuery, Map map);

     RemoteResult addContractInfo(AddContractInfoParam param);

     RemoteResult<Pact> getPactInfo(String orderCode,String userId,int shopId);

     RemoteResult uploadFile(byte[] buf,String lenovoId, String cId);

}
