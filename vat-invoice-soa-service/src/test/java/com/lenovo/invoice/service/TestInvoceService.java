package com.lenovo.invoice.service;

import com.lenovo.invoice.api.InvoiceApiService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayan3 on 2016/8/25.
 */
public class TestInvoceService  extends BaseManagerTestCase {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceApiService invoiceApiService;
    @Test
    public void checkVatInvoiceInfo(){
        try {
            List<Long> list=new ArrayList<Long>();
            list.add(5422L);
            System.out.println(invoiceService.changeInvoiceOrderMapping(list, 7168L));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void bu(){

    }
}
