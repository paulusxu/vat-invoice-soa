package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.ChangeInvoiceHistory;

import org.apache.ibatis.annotations.Param;

public interface ChangeInvoiceHistoryMapper {

    int insertChangeInvoiceHistory(ChangeInvoiceHistory record);

    ChangeInvoiceHistory selectByPrimaryKey(Long id);




}