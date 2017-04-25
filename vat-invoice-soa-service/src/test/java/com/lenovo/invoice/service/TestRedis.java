package com.lenovo.invoice.service;

import com.lenovo.invoice.api.InvoiceApiService;
import com.lenovo.invoice.service.redisObject.RedisObjectManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayan3 on 2016/8/25.
 */
public class TestRedis extends BaseManagerTestCase {

    @Autowired
    private RedisObjectManager redisObjectManager;
    @Test
    public void checkVatInvoiceInfo(){
        try {
            redisObjectManager.setString("11","nidaye");
            System.out.println(redisObjectManager.getString("11"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void bu(){

    }
}
