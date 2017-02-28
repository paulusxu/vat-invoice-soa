package com.lenovo.invoice.service;

import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.ordercenter.soa.domain.increaseorder.InvoiceOpen;

import java.util.List;
import java.util.Map;

/**
 * Created by mayan3 on 2016/6/28.
 */
public interface InvoiceService {
    //修改增票信息 抛btcp审核
    public boolean checkVatInvoiceInfo(List<VatInvoice> vatInvoiceList);
    public boolean checkVatInvoiceInfoNew(List<Invoice> vatInvoiceList);

    //增票订单查询
    public InvoiceOpen getIncreaseOrderList(String id, String rows, String page);
    //
    public int updateVatInvoiceBatch(UpdateVatInvoiceBatchParam param) throws Exception;

    /**
     * 分页查询
     * @param pageQuery
     * @param map
     * @return
     */
    public PageModel2<VatInvoice> getVatInvoicePage(PageQuery pageQuery, Map map);

    public List<VatInvoice> getVatInvoiceList(String ids);

    public void updateIsCheck(String ids);

    public List<VatInvoice> getExportVatInvoiceList(Map map);

    //更改发票与订单映射关系
    public RemoteResult changeInvoiceOrderMapping(List<Long> list, Long zid);

    public String getInvoiceOwer(String zid, String shopid);

    public List<VatInvoice> getVatInvoiceValidationList(Long id, String TaxNo, String CustomerName, String type, String faid);

    //支付后修改增票 为有效
    public void updateIsvalid(String zid);


}
