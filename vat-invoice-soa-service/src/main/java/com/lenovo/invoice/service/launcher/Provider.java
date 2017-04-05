package com.lenovo.invoice.service.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Provider {
    private static final Logger logger = LoggerFactory.getLogger(Provider.class);
    private static volatile boolean running = true;
    private static ApplicationContext ctx;

    public static void main(String[] args){
        try{
            ctx = new ClassPathXmlApplicationContext(
                    new String[]{
                            "classpath*:spring-config.xml",
                            "classpath*:spring-config-resource.xml",
                            "classpath*:spring-config-service.xml",
                            "classpath*:spring-config-dao.xml",
                            "classpath*:spring-config-dubbo.xml",
                            "classpath*:spring-redis-shard.xml",
                            "classpath*:spring-config-kafka.xml",
                            "classpath*:applicationContext-quartz.xml"
                    }
            );
            logger.info("vat-invoice-soa server started.");
        }catch (Exception e){
            running  = false;
            e.printStackTrace();
        }

        synchronized (Provider.class) {
            while (running) {
                try {
                    Provider.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }
}
