package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceService {

    public RemoteResult addCommonInvoice(CommonInvoice commonInvoice,Tenant tenant) throws Exception;

    public RemoteResult<CommonInvoice> getCommonInvoice(CommonInvoice commonInvoice,Tenant tenant);

    public RemoteResult<CommonInvoice> getCommonInvoiceByIds(String lenovoId,Integer id,Tenant tenant);

    //public RemoteResult updateCommonInvoice(Integer id,String invoiceTitle);

    //以下为添加税号后方法。这里只操作普票和电子票

    //分页查询，后台审核使用
    public RemoteResult<PageModel2<VatInvoice>> getInvoiceByPage(PageQuery pageQuery,VatInvoice vatInvoice);

    //查询单个发票信息
    public RemoteResult<VatInvoice> getInvoiceById(Long id);

    //前台页面，保存发票信息
    public RemoteResult<VatInvoice> saveInvoice(VatInvoice vatInvoice,Tenant tenant);

    //前台页面根据发票抬头带出发票信息，必须是已审核的
    public RemoteResult<VatInvoice> getInvoiceByTitle(VatInvoice vatInvoice,Tenant tenant);

}
