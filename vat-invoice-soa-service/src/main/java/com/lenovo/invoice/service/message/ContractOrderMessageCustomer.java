package com.lenovo.invoice.service.message;

import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.service.ContractService;
import com.lenovo.invoice.service.message.param.ContractParam;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mayan3 on 2016/8/9.
 */
public class ContractOrderMessageCustomer {
    private static final Logger LOGGER = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.contract");

    private KafkaConsumer kafkaConsumer;
    private ContractService contractService;

    public ContractOrderMessageCustomer(KafkaConsumer kafkaConsumer, ContractService contractService) {
        this.kafkaConsumer = kafkaConsumer;
        this.contractService=contractService;
        this.kafkaConsumer.start(new ConsumerHandler());
    }

    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String info) {
            LOGGER.info("ContractOrderMessageCustomer Start:" + info);
            try {
                ContractParam param=JacksonUtil.fromJson(info,ContractParam.class);
                contractService.getContractInfo(String.valueOf(param.getOrderId()),param.getLenovoId(),param.getShopId());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
