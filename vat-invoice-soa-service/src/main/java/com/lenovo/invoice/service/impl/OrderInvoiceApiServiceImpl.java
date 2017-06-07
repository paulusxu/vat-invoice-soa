package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.OrderInvoiceApiService;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.OrderInvoiceMapper;
import com.lenovo.invoice.domain.OrderInvoice;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mayan3 on 2017/6/7.
 */
@Service("orderInvoiceApiService")
public class OrderInvoiceApiServiceImpl implements OrderInvoiceApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.OrderInvoiceApiService");

    @Autowired
    private OrderInvoiceMapper orderInvoiceMapper;

    @Override
    public RemoteResult<Boolean> initOrderInvoice(OrderInvoice orderInvoice) {
        RemoteResult<Boolean> remoteResult = new RemoteResult(false);
        LOGGER.info("InitOrderInvoice Start:{}", JacksonUtil.toJson(orderInvoice));
        int rows = 0;
        try {
            rows = orderInvoiceMapper.insertOrderInvoice(orderInvoice);
            if (rows > 0) {
                remoteResult.setSuccess(true);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return remoteResult;
    }
}
