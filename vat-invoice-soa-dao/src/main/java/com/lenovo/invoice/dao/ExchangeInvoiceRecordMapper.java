package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.m2.arch.framework.domain.PageModel;
import com.lenovo.m2.arch.framework.domain.PageQuery;

import java.util.List;

/**
 * Created by admin on 2017/3/20.
 */
public interface ExchangeInvoiceRecordMapper {

    //添加换票记录
    public int addExchangeInvoiceRecord(ExchangeInvoiceRecord exchangeInvoiceRecord);

    //根据id获取换票记录
    public ExchangeInvoiceRecord getExchangeInvoiceRecord(String id);

    //修改换票记录的状态
    public int updateExchangeInvoiceRecord(ExchangeInvoiceRecord exchangeInvoiceRecord);

    //修改换票记录的BTCP号
    public int updateRecordBTCPCode(ExchangeInvoiceRecord exchangeInvoiceRecord);

    //获取换票记录，条件查询，不加分页，用于导出记录excel
    public List<ExchangeInvoiceRecord> getExchangeInvoiceRecordList(ExchangeInvoiceRecord exchangeInvoiceRecord);

    //获取换票记录，加分页
    public PageModel<ExchangeInvoiceRecord> getExchangeInvoiceRecordByPage(PageQuery pageQuery,ExchangeInvoiceRecord exchangeInvoiceRecord);

}
