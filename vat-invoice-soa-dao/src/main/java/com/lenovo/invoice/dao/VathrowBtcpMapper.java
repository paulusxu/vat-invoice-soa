package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VathrowBtcpMapper {

    int updateByOrderCode(@Param("orderCode") String orderCode, @Param("status") int status, @Param("msg") String msg);

    int deleteByPrimaryKey(Integer id);

    int insertVathrowBtcp(VathrowBtcp record);

    VathrowBtcp selectByPrimaryKey(Integer id);

    List<VathrowBtcp> getVatInvoice2BtcpList(String zid);

    int updateOrderStatus(@Param("orderCode") String orderCode, @Param("status") int status);

    int updateThrowingStatus(@Param("orderCode") String orderCode, @Param("status") int status);

    int updateVathrowbtcp(@Param("record") VathrowBtcp record);

    List<VathrowBtcp> getThrowBtcpList();

    List<VatInvoice> getNotThrowBtcpVatInvoicePage(Map map);

    int getNotThrowBtcpVatInvoiceCount(Map map);

    long updateZid(@Param("record") VatInvoice record);

    long updateVatInvoice(@Param("zid") String zid);

}