package com.lenovo.invoice.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lenovo.invoice.common.utils.math.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by mayan3 on 2017/7/4.
 */
public class AutoCheckInvoiceUtil {

    private static final Logger logger = LoggerFactory.getLogger("com.lenovo.invoice.worker.AutoCheckInvoice");

    private static final String uid = "A6427801";
    private static final String api = "100131";
    private static final String key = "a9ae2927379021b7d518f0ac52cef8c5";
    private static final String url = "http://open.yscredit.com/api/request";

    public static String getTaxNo(String entName) throws Exception {
        String resultStr=null;
        try {
            Map<String, String> mapParam = Maps.newHashMap();
            mapParam.put("entName", entName);

            String sign = Coder.sign("uid=" + uid + "&api=" + api + "&args=" + JsonUtil.toJsonString(mapParam) + "&key=" + key);
            String requestUrl = url + "?uid=" + uid + "&api=" + api + "&args=" + URLEncoder.encode(JsonUtil.toJsonString(mapParam), "UTF-8") + "&sign=" + sign;

            String result = HttpUtil.doGet(requestUrl, null);
            JSONObject jsonObject = JSONObject.parseObject(result);
            jsonObject = jsonObject.getJSONObject("data");
            resultStr=jsonObject.getString("creditCode");
            logger.info("AutoCheckInvoiceUtil :{},{}",entName,result);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }

        return resultStr;
    }
}
