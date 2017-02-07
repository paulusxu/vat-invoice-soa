package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.InvoiceShop;
import com.lenovo.invoice.domain.InvoiceShopModifyLog;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface InvoiceShopMapper {
    public int addInvoiceShop(InvoiceShop invoiceShop);

    public int editInvoiceShop(InvoiceShop invoiceShop);

    public int delInvoiceShop(@Param("ids")String ids,@Param("lenovoID")String lenovoID);

    public List<InvoiceShop> queryInvoiceShop(@Param("lenovoid")String lenovoid);

    public InvoiceShop queryInvoiceForId(@Param("invoiceId")String id,@Param("lenovoid")String lenovoid);

    public List<InvoiceShopModifyLog> queryInvoiceLog(@Param("invoiceCount")String count);

    public int updateLog(@Param("ids")String ids);

    public int getIdByUUID(@Param("uuid")String uuid);

    public int addInvoiceShopLog(InvoiceShopModifyLog invoiceShopModifyLog);

    public int editInvoiceIsDefault(@Param("lenovoid")String lenovoid);

    public int validationInvoice(@Param("lenovoid")String lenovoid,@Param("customerName")String customerName,@Param("taxNo")String taxNo);

}