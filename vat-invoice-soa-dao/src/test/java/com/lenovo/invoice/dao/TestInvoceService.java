package com.lenovo.invoice.dao;

import com.google.common.collect.Maps;
import com.lenovo.invoice.domain.VatInvoice;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by mayan3 on 2016/8/25.
 */
public class TestInvoceService  extends BaseManagerTestCase {

    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;
    @Test
    public void checkVatInvoiceInfo(){
        try {
            Map map= Maps.newHashMap();
            map.put("pageIndex", 0);//0
            map.put("pageSize", 10);//10
            map.put("customername","LLL");
            List<VatInvoice> invoiceList=vathrowBtcpMapper.getNotThrowBtcpVatInvoicePage(map);
            System.out.println();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void bu(){

    }
}
