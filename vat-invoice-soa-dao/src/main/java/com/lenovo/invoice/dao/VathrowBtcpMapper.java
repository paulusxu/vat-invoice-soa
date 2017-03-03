package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.VathrowBtcp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VathrowBtcpMapper {

    int updateByOrderCode(@Param("orderCode") String orderCode,@Param("status") int status,@Param("msg") String msg);

    int deleteByPrimaryKey(Integer id);

    int insert(VathrowBtcp record);


    VathrowBtcp selectByPrimaryKey(Integer id);

    List<VathrowBtcp> getVatInvoice2BtcpList(String zid);

    int updateOrderStatus(@Param("orderCode") String orderCode,@Param("status") int status);
}