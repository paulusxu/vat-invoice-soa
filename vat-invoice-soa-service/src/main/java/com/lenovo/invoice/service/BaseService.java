package com.lenovo.invoice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by mayan3 on 2016/6/22.
 */
public class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected boolean isNull(Object... obj){
        return !isNotNull(obj);
    }

    protected boolean isNotNull(Object... obj){
        if(obj != null){
            for (Object o : obj) {
                if(o == null){
                    return false;
                }
            }
        }else{
            return false;
        }
        return true;
    }

    protected Properties loadProperty() {
        Properties prop = new Properties();
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("other.properties");
            prop.load(is);

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return prop;
    }
}
