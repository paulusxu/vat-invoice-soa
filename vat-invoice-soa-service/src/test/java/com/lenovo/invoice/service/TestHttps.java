package com.lenovo.invoice.service;

import com.lenovo.invoice.common.utils.HttpConnectionUtil;
import com.lenovo.invoice.common.utils.HttpUtil;
import com.lenovo.invoice.common.utils.MD5;
import com.lenovo.invoice.common.utils.NetWorkWrapperUtil;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.VathrowBtcp;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/7/5.
 */
public class TestHttps extends BaseManagerTestCase {

    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;



    @Test
    public void run() {
        VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode("13426105");

        String xml = "";
        String context = "<CUSTOMERNAME>" + vathrowBtcp.getTitle() + "</CUSTOMERNAME>";
        context += "<TAXNO>" + vathrowBtcp.getTaxpayeridentity() + "</TAXNO>";
        context += "<BANKNAME>" + vathrowBtcp.getDepositbank() + "</BANKNAME>";
        context += "<ACCOUNTNO>" + vathrowBtcp.getBankno() + "</ACCOUNTNO>";
        context += "<ADDRESS>" + vathrowBtcp.getRegisteraddress() + "</ADDRESS>";
        context += "<PHONENO>" + vathrowBtcp.getRegisterphone() + "</PHONENO>";
        context += "<BTCPSO>" + vathrowBtcp.getOutid() + "</BTCPSO>";
        context += "<TAKERNAME>" + vathrowBtcp.getName() + "</TAKERNAME>";

        context += "<TAKERAREA>" + vathrowBtcp.getProvinceid() + "</TAKERAREA>";
        context += "<TAKERCITY>" + vathrowBtcp.getCity() + "</TAKERCITY>";
        context += "<TAKERCOUNTY>" + vathrowBtcp.getCounty() + "</TAKERCOUNTY>";

        context += "<TAKERADDRESS>" + vathrowBtcp.getAddress() + "</TAKERADDRESS>";
        context += "<TAKERPHONE>" + vathrowBtcp.getPhone() + "</TAKERPHONE>";
        context += "<TAKERPOST>" + vathrowBtcp.getZip() + "</TAKERPOST>";
        context += "<ISMERGEINVOICE>" + vathrowBtcp.getIsneedmerge() + "</ISMERGEINVOICE>";
        context += "<PURCHASEID>" + vathrowBtcp.getMembercode() + "</PURCHASEID>";
        xml = "<VATTAX>" + context + "</VATTAX>";
        /*String xml = "BBBBBBBBB11111111BBBBBBB22222222222222222222222222222222222222222222\n" +
                "22222222222222\n" +
                "15010838669N000995511qq010北京昌平222111333111222134567890121234561lvgt1@lenovo.com";*/
        String data_digest = MD5.sign(xml, "abc123", "utf-8");

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("xml", xml);
        paramMap.put("cid", "officialportal");
        paramMap.put("data_digest", data_digest);
        NetWorkWrapperUtil net = new NetWorkWrapperUtil();
        try {

            /*String s2 = HttpUtil.GetRequest("https://btcpws.lenovouat.cn/btcpws/AddVATTaxSimple", paramMap);
            System.out.println(s2);


            String url = "http://10.120.124.186:8080/btcpws/AddVATTaxSimple" + "?xml=" + xml + "&cid=officialportal&data_digest=" + data_digest;

            String s1 = net.requestData("http://10.120.124.186:8080/btcpws/AddVATTaxSimple", paramMap);
            System.out.println(s1);

            String s = HttpUtil.GetRequest(url, null);
            System.out.println(s);

            System.out.println(url);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
