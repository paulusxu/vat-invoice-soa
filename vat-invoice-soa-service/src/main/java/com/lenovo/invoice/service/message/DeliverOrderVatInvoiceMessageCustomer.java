package com.lenovo.invoice.service.message;

import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 发货后可抛单
 * Created by mayan3 on 2017/3/3.
 */
public class DeliverOrderVatInvoiceMessageCustomer {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.deliver");


    private KafkaConsumer kafkaConsumer;
    private VatInvoiceService vatInvoiceService;

    public DeliverOrderVatInvoiceMessageCustomer(KafkaConsumer kafkaConsumer, VatInvoiceService vatInvoiceService) {
        this.kafkaConsumer = kafkaConsumer;
        this.vatInvoiceService = vatInvoiceService;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String msg) {
            LOGGER.info("DeliverOrderVatInvoiceMessageCustomer Start:" + msg);
            int rows = 0;
            try {
                Map map= JacksonUtil.fromJson(msg, Map.class);
                rows = vatInvoiceService.updateOrderStatus((Long)map.get("orderCode")+"" , 3);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            LOGGER.info("DeliverOrderVatInvoiceMessageCustomer End:{},{}", msg, rows);
        }
    }
}
