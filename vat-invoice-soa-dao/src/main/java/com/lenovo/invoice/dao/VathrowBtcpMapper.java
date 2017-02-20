package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.VathrowBtcp;
import org.apache.ibatis.annotations.Param;

public interface VathrowBtcpMapper {

    int updateByOrderCode(@Param("orderCode") String orderCode,@Param("status") int status);

    int deleteByPrimaryKey(Integer id);


    int insert(VathrowBtcp record);

    int insertSelective(VathrowBtcp record);


    VathrowBtcp selectByPrimaryKey(Integer id);



}