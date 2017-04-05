package com.lenovo.invoice.service.message;

import com.lenovo.invoice.service.ContractService;
import com.lenovo.invoice.service.InvoiceService;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订单抛单后的订单进增票池子
 * Created by mayan3 on 2016/9/27.
 */
public class ThrowOrderVatInvoiceMessageCustomer {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.throw");

    private KafkaConsumer kafkaConsumer;
    private VatInvoiceService vatInvoiceService;

    public ThrowOrderVatInvoiceMessageCustomer(KafkaConsumer kafkaConsumer, VatInvoiceService vatInvoiceService) {
        this.kafkaConsumer = kafkaConsumer;
        this.vatInvoiceService=vatInvoiceService;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String orderCode) {
            LOGGER.info("ThrowOrderVatInvoiceMessageCustomer Start:" + orderCode);
            try {
                vatInvoiceService.parseInvoice(orderCode);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    }
}
