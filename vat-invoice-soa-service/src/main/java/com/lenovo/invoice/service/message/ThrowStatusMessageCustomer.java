package com.lenovo.invoice.service.message;

import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.invoice.service.message.param.ThrowStatusParam;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 是否可抛送增票 订单推送
 * Created by mayan3 on 2017/3/7.
 */
public class ThrowStatusMessageCustomer {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.throwStatus");

    private KafkaConsumer kafkaConsumer;
    private VatInvoiceService vatInvoiceService;

    public ThrowStatusMessageCustomer(KafkaConsumer kafkaConsumer, VatInvoiceService vatInvoiceService) {
        this.kafkaConsumer = kafkaConsumer;
        this.vatInvoiceService = vatInvoiceService;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String msg) {
            LOGGER.info("ThrowStatusMessageCustomer Start:" + msg);
            int rows = 0;
            try {
                ThrowStatusParam param = JacksonUtil.fromJson(msg, ThrowStatusParam.class);
                rows = vatInvoiceService.updateThrowingStatus(param.getOrderId(), param.getStatus());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("ThrowStatusMessageCustomer End:{},{}", msg, rows);

        }
    }
}
