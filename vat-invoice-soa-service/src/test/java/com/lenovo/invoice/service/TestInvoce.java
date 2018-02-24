package com.lenovo.invoice.service;

import com.lenovo.invoice.api.InvoiceApiService;
import com.lenovo.invoice.domain.param.FaData;
import com.lenovo.invoice.domain.param.GetCiParam;
import com.lenovo.invoice.domain.result.ConfigurationInformation;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuweihua on 2018/1/19.
 */
public class TestInvoce extends BaseServiceTest{
    @Autowired
    private InvoiceApiService invoiceApiService;

    @Test
    public void getConfigurationInformation(){
        try{
            GetCiParam getCiParam=new GetCiParam();
            getCiParam.setBigDecimal(new BigDecimal(5293));
            getCiParam.setShopId(0);
            getCiParam.setBu(10);
            getCiParam.setSalesType(0);
            getCiParam.setSilenceOrder(false);
            List<FaData> faDatas=new ArrayList<FaData>();
            FaData faData=new FaData("7ef1d628-5bd3-4651-9530-793678cc02af",0);
            faDatas.add(faData);
            getCiParam.setFaDatas(faDatas);
            Tenant tenant=new Tenant();
            tenant.setShopId(1);
            tenant.setCurrencyCode("CNY");
            tenant.setLanguage("zh");
            RemoteResult<ConfigurationInformation> sss=invoiceApiService.getConfigurationInformation(getCiParam, tenant);
            System.out.println(sss);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
