package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.*;
import com.lenovo.invoice.domain.result.ConfigurationInformation;
import com.lenovo.invoice.domain.result.FaInvoiceResult;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import com.lenovo.m2.buy.order.middleware.domain.btcp.IncreaseOrderRequest;

import java.util.List;
import java.util.Map;

/**
 * 官网增票管理
 * Created by mayan3 on 2016/6/20.
 */
public interface InvoiceApiService {
    //获取增票信息
    RemoteResult<GetVatInvoiceInfoResult> getVatInvoiceInfo(GetVatInvoiceInfoParam param, Tenant tenant);

    //获取增票信息
    RemoteResult<List<GetVatInvoiceInfoResult>> getVatInvoiceInfo(GetVatInvoiceInfoListParam param, Tenant tenant);

    //添加或修改增票信息
    RemoteResult addVatInvoiceInfo(AddVatInvoiceInfoParam param, Tenant tenant);
    //添加或修改增票信息 换票专用
    RemoteResult addVatInvoiceInfoForChange(AddVatInvoiceInfoParam param, Tenant tenant);

    //订单提交校验接口
    RemoteResult checkVatInvoiceInfo(String id, String lenovoId, String region, Tenant tenant);

    //btcp审核是否通过-
    RemoteResult changeVatInvoiceState(String id, boolean isThrough, Tenant tenant);

    RemoteResult queryVatInvoiceInfo(String id);

    RemoteResult<List<VatInvoice>> queryVatInvoiceInfo(List<String> listZid);

    RemoteResult<Boolean> throwVatInvoice2BTCP(String zid, String orderCodes);

    String getType(String faid, String faType);

    //获取准备抛送btcp的列表
    List<VathrowBtcp> getThrowBtcpList();

    //抛送btcp的列表
    void throwBTCP(List<VathrowBtcp> btcpList);

    //admin后台获取某个增票下订单列表
    PageModel2<VathrowBtcp> getOrderListByZidPage(PageQuery pageQuery, Map map);

    PageModel2<VatInvoice> getNotThrowBtcpVatInvoicePage(PageQuery pageQuery, Map map);

    //合并zid,此zid下的订单一起合并
    long updateZid(List<Long> listZids, String zid);

    //补单
    long makeUpVatInvocie(String orderCode);
    //是否可抛送增票 订单推送
    int updateThrowingStatus(String orderCode, int status);

    //更改是否有效isvalid
    int updateIsvalid(int valid);
    //修改单条增票
    long updateVatInvoice(UpdateVatInvoiceBatchParam param);

    //btcp同步增票
    long btcpSyncVatInvoice(IncreaseOrderRequest increaseOrderRequest);

    /**
     * 获取可开具的发票
     *
     * @param getInvoiceTypeParam
     * @return
     */
    RemoteResult<List<FaInvoiceResult>> getInvoiceTypes(GetInvoiceTypeParam getInvoiceTypeParam);

    RemoteResult<List<FaInvoiceResult>> getInvoiceTypes(GetInvoiceTypeParam getInvoiceTypeParam, Tenant tenant);

    RemoteResult<ConfigurationInformation> getConfigurationInformation(GetCiParam getCiParam, Tenant tenant);

    //自动审核普票
    void autoCheckInvoice();



}
