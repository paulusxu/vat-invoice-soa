package com.lenovo.invoice.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HttpClient 工具�??
 * Created with IntelliJ IDEA.
 * User: jijun.yang
 * Date: 13-6-19
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
public class NetWorkWrapperUtil {

    private static final Logger log = Logger.getLogger(NetWorkWrapperUtil.class);
    private static final Logger logger = Logger.getLogger(NetWorkWrapperUtil.class);

    private HttpGet mHttpGet = null;

    private HttpPost mHttpPost = null;

    private DefaultHttpClient mHttpClient = null;

    public static final String HTTP_TYPE_GET = "GET";

    public static final String HTTP_TYPE_POST = "POST";

    public static final int REQUEST_TIMEOUT = 1000 * 50;

    protected String mHttpType = "GET";


    public void disconnect() {
        try {
            if (mHttpGet != null) {
                if (!mHttpGet.isAborted()) {
                    mHttpGet.abort();
                }
            }
            if (mHttpPost != null) {
                if (!mHttpPost.isAborted()) {
                    mHttpPost.abort();
                }
            }
            mHttpClient.getConnectionManager().shutdown();
        } catch (Exception e) {

        }
    }


    public String requestData(String url,
                              Map<String, String> params) throws Exception {
        String data = "";
        String responsedata = "";
        StringBuilder sb = new StringBuilder();
        int pre = (int) System.currentTimeMillis();
        //请求�??始时�??
        //   sb = zzstartBuilder(sb, url, headerMap, params);
        //请求参数
        String param = "";
        //返回�??
        int code = 200;
        try {


            mHttpPost = new HttpPost(url);
            // setHttpHeaders(mHttpPost, headerMap);
            if (params != null) {
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                Iterator<String> iterator = params.keySet().iterator();
                String key;
                String buffer = "?";
                while (iterator.hasNext()) {
                    key = iterator.next();
                    log.debug(key + " = " + params.get(key));
                    pairs.add(new BasicNameValuePair(key, params.get(key)));
                    buffer = buffer + key + "=" + params.get(key) + "&";
                }
                sb.append("param:" + param + ";");
                UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairs, "UTF-8");
                mHttpPost.setEntity(uefe);
            }
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, REQUEST_TIMEOUT);
            mHttpClient = new DefaultHttpClient(httpParams);
            HttpResponse httpResponse = mHttpClient.execute(mHttpPost);
            code = httpResponse.getStatusLine().getStatusCode();
//                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                    throw new MobileApiException(httpResponse.getStatusLine().getReasonPhrase());
//                }
            HttpEntity httpEntity = httpResponse.getEntity();

            data = EntityUtils.toString(httpEntity, "UTF-8");


            responsedata = new String(Base64.encodeBase64(data.getBytes()), "UTF-8");
            //sb.append("responsedata:" + base64str + ";");
            sb.append("exceptioncode:" + code + ";");

        } catch (MalformedURLException e) {
            log.error("MalformedURLException url=" + url, e);
            sb.append("exceptioncode:" + code + ";");
            throw e;
        } catch (SocketTimeoutException e) {
            //408
            log.error("SocketTimeoutException url=" + url);
            sb.append("exceptioncode:" + 408 + ";");
            throw e;
        } catch (ConnectTimeoutException e) {
            log.error("ConnectTimeoutException url=" + url);
            sb.append("exceptioncode:" + 40801 + ";");
            throw e;
        } catch (HttpException e) {
            log.error("HttpException url=" + url, e);
            sb.append("exceptioncode:" + 801 + ";");
            throw e;
        } catch (IOException e) {
            log.error("IOException url=" + url, e);
            sb.append("exceptioncode:" + 802 + ";");
            throw e;
        } finally {
            //    zzendBuilder(startDate, sb, pre);
            sb.append("responsedata:" + responsedata + ";");
            log.info(sb.toString());
        }
//        if (data.contains("error")) {
//            log.error("服务器错?-->" + data);
//        }
        if (log.isDebugEnabled()) {
            log.debug("data:" + data);
        }
        return data;
    }

    private StringBuilder zzstartBuilder(StringBuilder sb, String url,
                                         Map<String, String> headerMap, Map<String, String> params) throws UnknownHostException {
        //请求url
        String requertUrl = url.substring(7, url.length());
//        //本机ip
//        InetAddress addr = InetAddress.getLocalHost();
//        String ip = addr.getHostAddress().toString();//获得本机IP
//        sb.append("ip:" + ip + ";");

        sb.append("url:" + requertUrl).append(";");
        sb.append("httptype:" + mHttpType + ";");
        //公共参数
        String userSession = headerMap.get("Usersession");
        String heardrInfo = headerMap.get("headerInfo");
        String mid = headerMap.get("Mid");


        sb.append(heardrInfo);
        if (userSession == null) {
            userSession = "";
        }
        sb.append(";userSession:" + userSession + ";");
        sb.append("mid:" + mid + ";");
        headerMap.remove("headerInfo");

        return sb;
    }


    private static String readTextFile(InputStream inputStream) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024 * 2];
        int len;
        try {
            int i = 0;
            while ((len = inputStream.read(buf)) != -1) {
                i++;
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return new String(outputStream.toByteArray());
    }


    private void setHttpHeaders(AbstractHttpMessage httpMessage, Map<String, String> headers) {
        Iterator<String> iterator = headers.keySet().iterator();
        String headerName = "";
        String headerValue = "";
        while (iterator.hasNext()) {
            headerName = iterator.next();
            if (!headerName.equals("headerData") && !headerName.equals("ActivityId")) {
                // headerName = Util.firstToUpper(headerName.trim());
            }
            headerValue = headers.get(headerName);
            if (headerValue != null) {
                httpMessage.setHeader(headerName, headerValue);
            }
            log.debug(headerName + "=" + headerValue + ",");
        }
        // httpMessage.setHeader("Udid", "0000000000");

    }

    public static void main(String[] args) {
        Map<String, String> paramMap = new HashMap<String, String>();
        Map<String, String> map = new HashMap<String, String>();
        String KEY = "abc123";
        String context = "<BTCPSO>" + "N000977835" + "</BTCPSO>";
        context += "<StateType>" + 1 + "</StateType>";
        context += "<StateReason>" + "a" + "</StateReason>";
        context += "<UserName>" + "zyg" + "</UserName>";

        String xml = "<Order>" + context + "</Order>";
        System.out.println("POST to BTCP  XML = " + xml);
        String data_digest = MD5.sign(xml, KEY, "utf-8");

        paramMap.put("xml", xml);
        paramMap.put("cid", "officialportal");
        paramMap.put("data_digest", data_digest);

        HttpGet mHttpGet = new HttpGet("");
        String url ="http://10.120.23.236:8080/btcpws/OrderStateUpdate";
        NetWorkWrapperUtil net = new NetWorkWrapperUtil();
        try {
        String resposeData = net.requestData(url, paramMap);

        map = XMLUtil.parseXml(resposeData);
        System.out.println("BTCP to POST result = [" + resposeData + "], map=[" + map + "]");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("抛�?�增票失败，xml:" + xml + "   失败原因�?" + e.getMessage(), e);
        }
    }
}
