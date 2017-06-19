package com.lenovo.invoice.service.message;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.kafka.api.core.consumer.KafkaConsumer;
import com.lenovo.kafka.api.core.handler.BaseConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by admin on 2017/6/14.
 * 将已支付订单所使用的普票和电子票置为有效
 */
public class UpdateInvoiceIsValidConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateInvoiceIsValidConsumer.class);

    private KafkaConsumer kafkaConsumer;

    @Autowired
    private CommonInvoiceMapper commonInvoiceMapper;

    public UpdateInvoiceIsValidConsumer(KafkaConsumer kafkaConsumer){
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaConsumer.start(new ConsumerHandler());
    }
    private class ConsumerHandler implements BaseConsumerHandler {
        @Override
        public void execute(String message) {
            LOGGER.info("订单支付后=普票和电子票置为有效消息=="+message);
            /*{"invoiceId":"String","orderCode":Long,"shopId":int,"type":int}0电子票1普票2增票*/
            try {
                JSONObject jsonObject = JSONObject.parseObject(message);
                Integer shopId = jsonObject.getInteger("shopId");
                 Integer type = jsonObject.getInteger("type");
                if ((shopId==1||shopId==3||shopId==14)&&(type==0||type==1)){
                    String invoiceId = jsonObject.getString("invoiceId");
                    int i = commonInvoiceMapper.updateInvoiceIsValid(Long.parseLong(invoiceId));
                    LOGGER.info("普票和电子票置为有效返回值"+i);
                }
            }catch (Exception e){
                LOGGER.error("普票和电子票置为有效出现异常=="+message+"=="+e.getMessage(),e);
            }
        }
    }
}
