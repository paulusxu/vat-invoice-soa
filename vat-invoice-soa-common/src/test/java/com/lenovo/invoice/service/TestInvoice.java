package com.lenovo.invoice.service;

import com.google.common.collect.Maps;
import com.lenovo.invoice.common.utils.HttpUtil;
import com.lenovo.invoice.common.utils.JsonUtil;
import com.lenovo.invoice.common.utils.math.Coder;

import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by mayan3 on 2017/7/3.
 */
public class TestInvoice {
    public static void main(String[] arg) throws Exception {

        String uid = "A6427801";
        String api = "100131";
        String key = "a9ae2927379021b7d518f0ac52cef8c5";

        String url = "http://open.yscredit.com/api/request";

        Map<String, String> mapParam = Maps.newHashMap();
        mapParam.put("entName", "联想(北京)有限公司");
//        {  "code" : "0001",  "msg" : "查询无记录",  "orderNo" : "1707041114382395804",  "data" : {    "result" : "3",    "resultMsg" : "查询无记录",    "creditCode" : ""  }}
//        {  "code" : "0000",  "msg" : "查询成功",  "orderNo" : "1707041115072775802",  "data" : {    "result" : "0",    "resultMsg" : "查询成功",    "creditCode" : "91110108700000458B"  }}


        String sign = Coder.sign("uid=" + uid + "&api=" + api + "&args=" + JsonUtil.toJsonString(mapParam) + "&key=" + key);

        String requestUrl = url + "?uid=" + uid + "&api=" + api + "&args=" + URLEncoder.encode(JsonUtil.toJsonString(mapParam), "UTF-8") + "&sign=" + sign;

        String result = HttpUtil.doGet("https://www.lenovo.com.cn/api/products/getscore?productcodes=59481", null);
//        JSONObject jsonObject=JSONObject.parseObject(result);
//        jsonObject=jsonObject.getJSONObject("data");
        System.out.println(result);
    }
}
