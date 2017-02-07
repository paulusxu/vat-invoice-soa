package com.lenovo.invoice.service;

import com.lenovo.invoice.api.ContractApiService;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.domain.Contract;
import com.lenovo.invoice.domain.param.AddContractInfoParam;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mayan3 on 2016/8/10.
 */
public class TestContractApiService extends BaseManagerTestCase {

    @Autowired
    private ContractApiService contractApiService;

    @Test
    public void upload() throws FileNotFoundException {
//        InputStream is=new FileInputStream(new File("d:/11.pdf"));
//        RemoteResult remoteResult=contractApiService.uploadFile(is, "108478", "SMB60822000A");
//        System.out.println(remoteResult);
    }


    @Test
    public void getContractPage(){
        try {
            PageQuery pageQuery=new PageQuery(10,1);
            Map<String,String> map=new HashMap<String,String>();
            map.put("lenovoId", "108408");
            PageModel2<Contract> pageModel2=contractApiService.getContractPage(pageQuery,map);
            System.out.println(JacksonUtil.toJson(pageModel2));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void checkVatInvoiceInfo(){
        try {
            String lenovoId="11";
            String cId="22";
            RemoteResult remoteResult=contractApiService.getContractInfo(lenovoId, cId);
            System.out.println(JacksonUtil.toJson(remoteResult));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void addContractInfo(){
        try {
            AddContractInfoParam param =new AddContractInfoParam();
            param.setLenovoId("11111");
            param.setShipAddress("11111");
            param.setBuyerName("11111");
            param.setTakeffectTime(new Date());
            param.setShipMobile("222222");
            param.setShipName("444444");
            param.setContractNo("44343");
            param.setIssend(true);

            RemoteResult remoteResult=contractApiService.addContractInfo(param);
            System.out.println(JacksonUtil.toJson(remoteResult));
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
