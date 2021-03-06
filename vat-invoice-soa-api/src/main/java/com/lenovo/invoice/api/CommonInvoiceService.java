package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.InvoiceToInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;

import java.util.List;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceService {

    /*public RemoteResult addCommonInvoice(CommonInvoice commonInvoice,Tenant tenant) throws Exception;

    public RemoteResult<CommonInvoice> getCommonInvoice(CommonInvoice commonInvoice,Tenant tenant);

    public RemoteResult<CommonInvoice> getCommonInvoiceByIds(String lenovoId,Integer id,Tenant tenant);*/

    //public RemoteResult updateCommonInvoice(Integer id,String invoiceTitle);

    //以下为添加税号后方法。这里只操作普票和电子票

    //分页查询，后台审核使用
    public RemoteResult<PageModel2<VatInvoice>> getInvoiceByPage(PageQuery pageQuery,VatInvoice vatInvoice);

    //后台审核通过接口
    public RemoteResult checkInvoice(VatInvoice vatInvoice);

    //后台审核拒绝接口
    public RemoteResult refuseInvoice(VatInvoice vatInvoice);

    //后台修改发票信息接口
    public RemoteResult updateInvoice(VatInvoice vatInvoice);

    //查询单个发票信息，只能查询到有效数据
    public RemoteResult<VatInvoice> getInvoiceById(Long id,Tenant tenant);

    //查询单个发票信息，支持使用废弃发票id查询
    public RemoteResult<VatInvoice> getInvoiceByIdForAll(Long id,Tenant tenant);

    //前台页面，保存发票信息
    public RemoteResult<VatInvoice> saveInvoice(VatInvoice vatInvoice,Tenant tenant);

    //前台页面根据发票抬头带出发票信息，必须是已审核的
    public RemoteResult<VatInvoice> getInvoiceByTitle(VatInvoice vatInvoice,Tenant tenant);

    //订单使用新增发票接口，如果已存在，返回老发票信息
    public RemoteResult<VatInvoice> addInvoice(VatInvoice vatInvoice,Tenant tenant);


    //伪删除发票
    public RemoteResult deleteInvoice(Long id,Tenant tenant);

    //添加废弃发票和有效发票的映射
    public RemoteResult addInvoiceToInvoice(InvoiceToInvoice invoice,Tenant tenant);

    //伪删除还原发票
    public RemoteResult rollbackDeleteInvoice(Long id,Tenant tenant);

    //删除废弃发票和有效发票的映射
    public RemoteResult deleteInvoiceToInvoice(Long id,Tenant tenant);

    //审核成功，将相同抬头的其他未审核发票废弃，添加映射
    public void deleteTheSameTitleInvoice(String customername,Long id);

    //查询该用户使用过的所有已审核公司普票和电票Lenovo，epp
    public RemoteResult<List<VatInvoice>> getInvoiceByUser(String lenovoId,Tenant tenant);

}
