package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.OrderInvoice;

public interface OrderInvoiceMapper {

    int insertOrderInvoice(OrderInvoice record);

    int updateByOrderId(OrderInvoice orderInvoice);

    int insertSelective(OrderInvoice record);

    OrderInvoice selectByPrimaryKey(Long orderid);

    int updateByPrimaryKeySelective(OrderInvoice record);

    int updateByPrimaryKey(OrderInvoice record);
}