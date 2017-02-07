package com.lenovo.invoice.common.utils;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by xuweihua on 2016/9/18.
 */
public class O2oFaIdUtil {
    static Properties config= new Properties();;
    static{
        try {

            config.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("o2ofaid.properties"), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return config.getProperty(name);
    }
}
