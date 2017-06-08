package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.ConsigneeApiService;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.Consignee;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mayan3 on 2017/5/15.
 */
@Service("consigneeApiService")
public class ConsigneeApiServiceImpl implements ConsigneeApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsigneeApiServiceImpl.class);
    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;

    @Override
    public RemoteResult<Boolean> updateConsignee(Consignee consignee) {
        LOGGER.info("updateConsignee:{}", JacksonUtil.toJson(consignee));
        RemoteResult<Boolean> remoteResult = new RemoteResult<Boolean>(false);
        try {
            int rows = vathrowBtcpMapper.updateConsignee(consignee);
            if (rows > 0) {
                remoteResult.setSuccess(true);
                remoteResult.setT(true);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return remoteResult;
    }
}
