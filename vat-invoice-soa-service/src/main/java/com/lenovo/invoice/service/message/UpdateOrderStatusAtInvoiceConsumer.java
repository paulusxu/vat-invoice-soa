package com.lenovo.invoice.service.message;

import com.lenovo.invoice.dao.InvoiceOrderMapper;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by admin on 2017/6/28.
 * 订单抛单后，将普票电子票和订单的映射中，订单状态改为已抛单
 */
public class UpdateOrderStatusAtInvoiceConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateOrderStatusAtInvoiceConsumer.class);

    private KafkaConsumer kafkaConsumer;

    @Autowired
    private InvoiceOrderMapper invoiceOrderMapper;

    public UpdateOrderStatusAtInvoiceConsumer(KafkaConsumer kafkaConsumer){
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String orderCode) {
            LOGGER.info("订单抛单后=修改订单状态为已抛单消息=="+orderCode);
            try {
                int i = invoiceOrderMapper.updateOrderStatus(Long.parseLong(orderCode));
                LOGGER.info("订单抛单后=修改订单状态为已抛单结果=="+orderCode+"=="+i);
            }catch (Exception e){
                LOGGER.error("修改订单状态为已抛单出现异常=="+orderCode+"=="+e.getMessage(),e);
            }
        }
    }
}
