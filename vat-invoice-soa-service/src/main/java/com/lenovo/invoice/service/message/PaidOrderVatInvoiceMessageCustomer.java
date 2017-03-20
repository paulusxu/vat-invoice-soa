package com.lenovo.invoice.service.message;

import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.service.ContractService;
import com.lenovo.invoice.service.InvoiceService;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 支付后的订单进增票池子
 * Created by mayan3 on 2016/9/27.
 */
public class PaidOrderVatInvoiceMessageCustomer {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.paid");

    private KafkaConsumer kafkaConsumer;
    private InvoiceService invoiceService;
    private VatInvoiceService vatInvoiceService;


    public PaidOrderVatInvoiceMessageCustomer(KafkaConsumer kafkaConsumer, InvoiceService invoiceService) {
        this.kafkaConsumer = kafkaConsumer;
        this.invoiceService = invoiceService;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String msg) {
            LOGGER.info("PaidOrderVatInvoiceMessageCustomer Start:" + msg);
            try {
                Map map = JacksonUtil.fromJson(msg, Map.class);
                String zid = (String) map.get("invoiceId");
                Integer orderCode = (Integer) map.get("orderCode");
                long rows = vatInvoiceService.initVathrowBtcp(orderCode+"");
                LOGGER.info("PaidOrderVatInvoiceMessageCustomer End:{},{}", msg, rows);
                invoiceService.updateIsvalid(zid);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
