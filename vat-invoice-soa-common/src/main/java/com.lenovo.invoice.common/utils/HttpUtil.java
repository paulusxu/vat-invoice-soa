package com.lenovo.invoice.common.utils;

import org.apache.http.*;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/11/16.
 */
public class HttpUtil {


    private static Logger logger = Logger.getLogger(HttpUtil.class);
    private static DefaultHttpClient httpClient;
    private static DefaultHttpClient httpClient_proxy;

    private static String proxyHost = "10.99.60.201";//B2CMainConfig.getProxyHost();// 代理ip地址
    private static int proxyPort = 8080;// B2CMainConfig.getProxyPort();// 代理端口

    private static int maxConLifeTimeMs = 300000;
    private static int defaultMaxConPerHost = 50;
    private static int maxTotalConn = 10000;
    private static int conTimeOutMs = 10000;
    private static int soTimeOutMs = 30000;

    static {
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
//	        TrustManager[] tm = { new MyX509TrustManager() };

            Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            //sslcontext.init(null, tm, new java.security.SecureRandom());
            sslcontext.init(null, null, null);
            SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//STRICT_HOSTNAME_VERIFIER
            Scheme https = new Scheme("https", 443, sf);
            SchemeRegistry sr = new SchemeRegistry();
            sr.register(http);
            sr.register(https);
            PoolingClientConnectionManager cm = new PoolingClientConnectionManager(sr, maxConLifeTimeMs, TimeUnit.MILLISECONDS);
            cm.setMaxTotal(maxTotalConn);
            cm.setDefaultMaxPerRoute(defaultMaxConPerHost);
            //普通http客户端
            httpClient = new DefaultHttpClient(cm);
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, conTimeOutMs);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOutMs);
            httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
            httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
                public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
                    HttpEntity entity = response.getEntity();
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null && ceheader.getValue().toLowerCase().contains("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                    }
                }
            });
            //代理http客户端
            httpClient_proxy = new DefaultHttpClient(cm);
            httpClient_proxy.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, conTimeOutMs);
            httpClient_proxy.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeOutMs);
            httpClient_proxy.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient_proxy.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        } catch (Exception e) {
            logger.error("HttpExecutor init error", e);
        }
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * @param request
     * @return
     * @throws Exception HttpResponse
     * @Description:有代理调用
     * @Created:lining 2013年12月26日下午4:57:33
     * @Modified:
     */
    public static HttpResponse executeProxy(HttpUriRequest request) throws Exception {
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient/*_proxy*/.execute(request);
        } catch (Exception e) {
            throw e;
        }
        return httpResponse;
    }
}
