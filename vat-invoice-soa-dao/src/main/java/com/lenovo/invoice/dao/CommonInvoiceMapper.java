package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.m2.arch.framework.domain.PageModel;
import com.lenovo.m2.arch.framework.domain.PageQuery;

/**
 * Created by admin on 2017/3/16.
 */
public interface CommonInvoiceMapper {

    public int addCommonInvoice(CommonInvoice commonInvoice);

    public CommonInvoice getCommonInvoiceById(Integer id);

    public int deleteCommonInvoice(Integer id);

    public CommonInvoice getCommonInvoiceByTitle(CommonInvoice commonInvoice);

    public int updateCommonInvoice(CommonInvoice commonInvoice);

    //以下为添加税号后方法。这里只操作普票和电子票

    //分页查询，后台审核使用
    public PageModel<VatInvoice> getInvoiceByPage(PageQuery pageQuery,VatInvoice vatInvoice);

    //查询单个发票信息
    public VatInvoice getInvoiceById(Long id);

    //修改发票信息，根据id，只能修改识别码类型，纳税人识别码，公司抬头，后台审核使用
    public int updateInvoice(VatInvoice vatInvoice);

    //审核通过，修改发票状态，后台审核使用
    public int updateInvoiceIsCheck(VatInvoice vatInvoice);

    //订单支付后，将发票修改为有效状态
    public int updateInvoiceIsValid(Long id);

    //前台页面，保存发票信息
    public int saveInvoice(VatInvoice vatInvoice);

    //前台页面根据发票抬头带出发票信息，必须是已审核的
    public VatInvoice getInvoiceByTitle(VatInvoice vatInvoice);

    //根据发票抬头，税号，开票方式和发票类型查询，用于判断发票是否已存在
    public VatInvoice invoiceIsExist(VatInvoice vatInvoice);

    //伪删除这张发票
    public int deleteInvoice(Long id);




}
