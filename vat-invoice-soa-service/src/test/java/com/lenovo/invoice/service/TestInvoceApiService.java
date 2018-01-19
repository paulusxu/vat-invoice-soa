package com.lenovo.invoice.service;

import com.lenovo.invoice.api.InvoiceApiService;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.domain.param.*;
import com.lenovo.invoice.domain.result.ConfigurationInformation;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mayan3 on 2016/6/23.
 */
public class TestInvoceApiService extends BaseManagerTestCase{
    @Autowired
    private InvoiceApiService invoiceApiService;
    private static final Logger loggerChange = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.InvoceApiServiceImpl");


    @Test
    public void checkVatInvoiceInfo(){
        GetVatInvoiceInfoListParam param=new GetVatInvoiceInfoListParam();
        param.setFaid("7102f80a-e544-4631-9f98-71c9e9296a5d");
        param.setFaType("1");
        param.setLenovoId("1000012107");
        Tenant tenant=new Tenant();
        tenant.setShopId(14);
        tenant.setCurrencyCode("CNY");
        tenant.setLanguage("zh");


        RemoteResult<List<GetVatInvoiceInfoResult>> resultRemoteResult=invoiceApiService.getVatInvoiceInfo(param, tenant);
        System.out.println(JacksonUtil.toJson(resultRemoteResult));
//        try {
//            String id="1";
//            String lenovoID="111";
//            System.out.println(invoiceApiService.checkVatInvoiceInfo(id,lenovoID));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

//    @Test
//    public void getVatInvoiceInfo(){
//        try {
//            GetVatInvoiceInfoParam param=new GetVatInvoiceInfoParam();
//            param.setLenovoId("10058824478");
//
//            param.setShopId(1);
//            RemoteResult remoteResult=invoiceApiService.getVatInvoiceInfo(param);
//            System.out.println(JacksonUtil.toJson(remoteResult));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void getVatInvoiceById(){
//        try {
////            String id="5";
////            String lenovoId="11";
////            RemoteResult remoteResult=invoiceApiService.checkVatInvoiceInfo(id, lenovoId);
////            System.out.println(JacksonUtil.toJson(remoteResult));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void changeVatInvoiceState(){
//        try {
//            String id="5";
//            boolean isThrough=true;
//            RemoteResult remoteResult=invoiceApiService.changeVatInvoiceState(id, isThrough);
//            System.out.println(JacksonUtil.toJson(remoteResult));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Test
//    public void addVatInvoiceInfo(){
//        try {
////            {"bankName":"工商银行","accountNo":"78787777887878654433","phoneNo":"18766143231","shard":false,"lenovoId":"10073739669","customerName":"联想","taxNo":"QWERTYUIOPASDFG","shopId":1,"isNeedMerge":true,"isShard":false,"address":"联想北研"}
//            AddVatInvoiceInfoParam param=new AddVatInvoiceInfoParam();
//            param.setAccountNo("78787777887878654433");
//            param.setAddress("联想北研xxx");
//            param.setBankName("工商银行xxx");
//            param.setTaxNo("QWERTYUIOPASDFG");
//            param.setIsShard(false);
//            param.setPhoneNo("13456789013");
//            param.setLenovoId("10073739669");
//            param.setCustomerName("联想");
//            param.setShopId(1);
//            param.setIsNeedMerge(true);
//            param.setFaid("7ef1d628-5bd3-4651-9530-793678cc02af");
//            RemoteResult remoteResult=invoiceApiService.addVatInvoiceInfo(param);
//            System.out.println(JacksonUtil.toJson(remoteResult));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

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
