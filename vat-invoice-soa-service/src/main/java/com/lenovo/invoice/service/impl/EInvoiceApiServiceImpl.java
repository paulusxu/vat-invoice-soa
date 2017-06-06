package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.EInvoiceApiService;
import com.lenovo.invoice.common.utils.HttpUtil;
import com.lenovo.invoice.common.utils.PropertiesUtil;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.tool.util.StringUtils;
import com.lenovo.m2.ordercenter.soa.api.constant.ResultCode;
import com.lenovo.m2.ordercenter.soa.domain.BaseInfo;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayan3 on 2017/4/25.
 */
@Service("eInvoiceApiService")
public class EInvoiceApiServiceImpl implements EInvoiceApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EInvoiceApiServiceImpl.class);
    public static final String CID = "mobilemall";
    public static final String DATA_DIGEST = "mobile56rm49";

    @Autowired
    private PropertiesUtil propertiesUtil;

    @Override
    public RemoteResult<BaseInfo> downLoadInvoice(String btcpCode, String itCode) {
        RemoteResult remoteResult = new RemoteResult(false);
        LOGGER.info("下载电子票开始 btcpCode[" + btcpCode + "]itCode[" + itCode + "]");

        String URL = propertiesUtil.getDownLoadInvoiceUrl();
//        String URL = "http://10.96.89.146:8080/btcpws/QueryEInvoice";
        if (URL != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<Order><ShipmentsNo>").append(btcpCode).append("</ShipmentsNo></Order>");
            try {
                HttpPost post = new HttpPost(URL);
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                String xml = buffer.toString();
                nvps.add(new BasicNameValuePair("xml", xml));
                // ”cid”为分配的客户ID
                nvps.add(new BasicNameValuePair("cid", CID));
                // “data_digest”字段进行签名验证
                MessageDigest md51 = MessageDigest.getInstance("MD5");
                String value = xml + DATA_DIGEST;
                value = com.lenovo.m2.ordercenter.soa.common.util.Base64.encode(md51.digest(value.getBytes("UTF-8")));
                nvps.add(new BasicNameValuePair("data_digest", value));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8");
                post.setEntity(entity);
                LOGGER.info("调用btcp接口开始====btcpCode："+btcpCode);
                HttpResponse response = HttpUtil.executeProxy(post);
                String result = EntityUtils.toString(response.getEntity());
                LOGGER.info("xml：" + xml + "##" + URL + "##" + DATA_DIGEST + "##" + CID + "##" + "data_digest:" + value + "##result:" + result);
                // 返回结果解析
                SAXReader saxReader = new SAXReader();
                saxReader.setEncoding("UTF-8");
                Document doc;
                doc = saxReader.read(new ByteArrayInputStream(result.trim().getBytes("UTF-8")));
                Element root = doc.getRootElement();
                if (root != null) {
                    String code = root.elementText("code");
                    String code1 = root.elementText("Code");
                    if (code != null || code1 != null) {
                        String message = root.elementText("Message");
                        LOGGER.info("btcpCode["+btcpCode+"]下载电子票接口：" + message);
                        remoteResult.setResultCode("500");
                        remoteResult.setResultMsg(root.elementText("Message"));
                        remoteResult.setT(new BaseInfo(500, root.elementText("Message")));
                    } else {
                        String url = root.elementText("PDF_URL");
                        LOGGER.info("btcpCode[" + btcpCode + "]电票url==========" + url);
                        if (StringUtils.isNotBlank(url)) {
                            //todo 调用订单接口
                        }else {
                            remoteResult.setSuccess(false);
                            remoteResult.setT(new BaseInfo(500, "btcp返回报文错误"));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(btcpCode+"电子票下载异常" + e);
                remoteResult.setT(new BaseInfo(Integer.valueOf(BaseInfo.ERROR), "系统异常"));
                remoteResult.setResultCode(ResultCode.EXCEPTION);
                remoteResult.setResultMsg("系统异常");
                return remoteResult;
            }
        }else{
            remoteResult.setT(new BaseInfo(Integer.valueOf(BaseInfo.ERROR), "获取不到电子票下载地址"));
            remoteResult.setResultMsg("获取不到电子票下载地址");
        }

        return remoteResult;
    }
}
