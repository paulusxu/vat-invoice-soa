package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.O2oVatInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
import com.lenovo.invoice.domain.param.GetVatInvoiceInfoListParam;
import com.lenovo.invoice.domain.param.GetVatInvoiceInfoParam;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;

import java.io.InputStream;
import java.util.List;

/**
 * 官网增票管理
 * Created by mayan3 on 2016/6/20.
 */
public interface InvoiceApiService {
    //获取增票信息
    RemoteResult<GetVatInvoiceInfoResult> getVatInvoiceInfo(GetVatInvoiceInfoParam param,Tenant tenant);
    //获取增票信息
    RemoteResult<List<GetVatInvoiceInfoResult>> getVatInvoiceInfo(GetVatInvoiceInfoListParam param, Tenant tenant);
    //添加或修改增票信息
    RemoteResult addVatInvoiceInfo(AddVatInvoiceInfoParam param,Tenant tenant);
    //订单提交校验接口
    RemoteResult checkVatInvoiceInfo(String id, String lenovoId,String region,Tenant tenant);
    //btcp审核是否通过-
    RemoteResult changeVatInvoiceState(String id, boolean isThrough,Tenant tenant);

    RemoteResult queryVatInvoiceInfo(String id);

    RemoteResult<List<VatInvoice>> queryVatInvoiceInfo(List<String> listZid);

    RemoteResult<Boolean> throwVatInvoice2BTCP(String zids);

    String getType(String faid);

    List<VathrowBtcp> getThrowBtcpList();

    void throwBTCP(List<VathrowBtcp> btcpList);

    long updateZid(List<Long> listZids, String zid);
}
