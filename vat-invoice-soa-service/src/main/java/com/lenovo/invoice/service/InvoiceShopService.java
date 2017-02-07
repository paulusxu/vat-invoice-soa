package com.lenovo.invoice.service;

import com.lenovo.invoice.domain.InvoiceShop;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;

import java.util.Map;

/**
 * Created by xuweihua on 2016/7/26.
 */
public interface InvoiceShopService {

    public PageModel2<InvoiceShop> getInvoiceShopPage (PageQuery pageQuery, Map map);

    public int addInvoiceShop(InvoiceShop invoiceShop);

    public int editInvoiceShop(InvoiceShop invoiceShop);

    public int delInvoiceShop(String ids,String lenovoID);
}
