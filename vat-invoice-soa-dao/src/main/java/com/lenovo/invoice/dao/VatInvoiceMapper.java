package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VatInvoiceMapper {
    List<VatInvoice> getVatInvoice(@Param("customerName") String customerName, @Param("taxNo") String taxNo, @Param("type") String type);

    List<VatInvoice> getVatInvoiceInfo(@Param("customerName") String customerName, @Param("taxNo") String taxNo, @Param("type") String type);

    VatInvoice getVatInvoiceBySelected(VatInvoice vatInvoice);

    long insertVatInvoiceInfo(VatInvoice vatInvoice);

    long updateVatInvoice(VatInvoice vatInvoice);
    long updateVatInvoiceAutoCheck(VatInvoice vatInvoice);

    VatInvoice getVatInvoiceInfoById(long id);

    long updateVatInvoiceCheckState(@Param("id") String id, @Param("state") int state);

    int updateVatInvoiceBatch(UpdateVatInvoiceBatchParam param);

    int insertVatInvoiceLogBatch(UpdateVatInvoiceBatchParam param);

    int getVatInvoiceCount(Map map);

    List<VatInvoice> getVatInvoicePage(Map map);

    List<VatInvoice> getVatInvoiceList(@Param("ids") String ids);

    void updateIsCheck(@Param("ids") String ids);

    List<VatInvoice> getExportVatInvoiceList(Map map);

    VatInvoice getVatInvoiceId(VatInvoice vatInvoice);

    long deleteVatInvoice(long id);

    String getInvoiceOwer(@Param("zid") String zid,@Param("shopid") String shopid);

    List<VatInvoice> queryVatInvoiceInfo(@Param("ids") String ids);

    void updateIsvalid(@Param("zid") String zid);

    long updateAutoIsCheck(@Param("id") long id,@Param("type") int type);

    void updateVatInvoiceIsvalid(@Param("zid") String zid,@Param("shopid") String shopid);

    List<VatInvoice> getVatInvoiceValidationList(@Param("vid")Long id, @Param("TaxNo")String TaxNo, @Param("CustomerName")String CustomerName,@Param("type")String type,@Param("faid")String faid);

    VatInvoice getVatInvoiceByZid(@Param("zid") String zid,@Param("shopid") String shopid);

    List<VatInvoice> getAutoCheckInvoice();

}