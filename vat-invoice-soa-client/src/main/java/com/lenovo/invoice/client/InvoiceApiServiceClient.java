package com.lenovo.invoice.client;

import com.lenovo.invoice.api.InvoiceApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * Created by mayan3 on 2016/6/20.
 */
public class InvoiceApiServiceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceApiServiceClient.class);
    private static final InvoiceApiServiceClient INSTANCE = new InvoiceApiServiceClient();
    private final ClassPathXmlApplicationContext ctx;

    private InvoiceApiServiceClient() {
        String filename = "invoice-client-consumer.xml";
        try {
            Properties props = PropertiesLoaderUtils.loadAllProperties("env.properties");
            String env = props.getProperty("env");
            if (env != null && "test".equals(env)) {
                filename = "invoice-client-consumer-test.xml";
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        this.ctx = new ClassPathXmlApplicationContext(filename);

    }

    public static InvoiceApiServiceClient getINSTANCE() {
        return INSTANCE;
    }

    public InvoiceApiService getInvoiceApiService() {
        return (InvoiceApiService) this.ctx.getBean("invoceApiService");
    }

}
