package com.lenovo.invoice.service.message;

import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.ExchangeInvoiceRecordMapper;
import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.api.model.forward.InvoiceChangeApi;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订单抛单后 订单推送 用来完善修改发票记录的BTCP号
 */
public class CompleteExchangeInvoiceRecordConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteExchangeInvoiceRecordConsumer.class);

    private KafkaConsumer kafkaConsumer;

    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;

    @Autowired
    private ExchangeInvoiceRecordMapper exchangeInvoiceRecordMapper;

    public CompleteExchangeInvoiceRecordConsumer(KafkaConsumer kafkaConsumer){
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String orderCode) {
            LOGGER.info("订单抛单后消息=="+orderCode);
            try {
                //获取老发票信息和订单信息
                LOGGER.info("获取订单信息==参数=="+orderCode);
                RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
                LOGGER.info("获取订单信息==返回值=="+ JacksonUtil.toJson(invoiceChangeApiByOrderId));
                InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
                if (invoiceChangeApi==null){
                    //获取老发票信息和订单信息失败
                    LOGGER.info("获取老发票信息和订单信息失败");
                    return;
                }
                ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                record.setOrderCode(orderCode);
                record.setBTCPOrderCode(invoiceChangeApi.getOutId());
                int i = exchangeInvoiceRecordMapper.updateRecordBTCPCode(record);
                LOGGER.info("补全换票记录结果=="+i+"=="+orderCode);
            }catch (Exception e){
                LOGGER.error("完善修改发票记录的BTCP号"+e.getMessage(),e);
            }
        }
    }
}
