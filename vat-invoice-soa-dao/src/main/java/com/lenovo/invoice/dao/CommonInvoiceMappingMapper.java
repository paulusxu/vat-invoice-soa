package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.CommonInvoice;

import java.util.List;

/**
 * Created by admin on 2017/3/16.
 * 已废除
 */
public interface CommonInvoiceMappingMapper {

    public int addCommonInvoiceMapping(CommonInvoice commonInvoice);

    //查询用户最新开的一张发票信息映射
    public CommonInvoice getCommonInvoiceMapping(CommonInvoice commonInvoice);

    //根据用户id和发票id查询映射
    public List<CommonInvoice> getCommonInvoiceMappingByIds(CommonInvoice commonInvoice);
}
