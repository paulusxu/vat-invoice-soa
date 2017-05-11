package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VathrowBtcpMapper {

    int updateByOrderCode(@Param("orderCode") String orderCode, @Param("status") int status, @Param("msg") String msg);

    int deleteByPrimaryKey(Integer id);

    int insertVathrowBtcp(VathrowBtcp record);

    VathrowBtcp selectByPrimaryKey(Integer id);

    //根据批量增票zid获取 准备抛送btcp的列表
    List<VathrowBtcp> getVatInvoice2BtcpListByZid(@Param("zid") String zid);
    //根据批量orderid获取 准备抛送btcp的列表
    List<VathrowBtcp> getVatInvoice2BtcpListByOrderCode(@Param("orderCodes") String orderCodes);

    int updateOrderStatus(@Param("orderCode") String orderCode, @Param("status") int status);

    int updateThrowingStatus(@Param("orderCode") String orderCode, @Param("status") int status);

    int updateVathrowbtcp(@Param("record") VathrowBtcp record);

    List<VathrowBtcp> getThrowBtcpList();

    List<VatInvoice> getNotThrowBtcpVatInvoicePage(Map map);

    List<VathrowBtcp> getOrderListByZidPage(Map map);

    int getNotThrowBtcpVatInvoiceCount(Map map);

    int getOrderListByZidPageCount(Map map);

    long updateZid(@Param("record") VatInvoice record);

    long updateVatInvoice(UpdateVatInvoiceBatchParam param);

}