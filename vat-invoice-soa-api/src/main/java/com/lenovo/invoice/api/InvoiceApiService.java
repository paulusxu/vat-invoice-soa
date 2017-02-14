package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.O2oVatInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
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
    RemoteResult<GetVatInvoiceInfoResult> getVatInvoiceInfo(GetVatInvoiceInfoParam param);
    //添加或修改增票信息
    RemoteResult addVatInvoiceInfo(AddVatInvoiceInfoParam param,Tenant tenant);
    RemoteResult addVatInvoiceInfo(AddVatInvoiceInfoParam param);
    //订单提交校验接口
    RemoteResult checkVatInvoiceInfo(String id, String lenovoId,String region,Tenant tenant);
    RemoteResult checkVatInvoiceInfo(String id, String lenovoId,String region);
    //btcp审核是否通过-
    RemoteResult changeVatInvoiceState(String id, boolean isThrough,Tenant tenant);
    RemoteResult changeVatInvoiceState(String id, boolean isThrough);

    RemoteResult queryVatInvoiceInfo(String id);

    RemoteResult<List<VatInvoice>> queryVatInvoiceInfo(List<String> listZid);

    String getFaType(String faid);

    RemoteResult throwVatInvoice2BTCP(String zids);
}
