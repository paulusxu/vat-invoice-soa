package com.lenovo.invoice.common.utils.math;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * Created by mayan3 on 2017/7/3.
 */
public class HttpUtil {
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	

	public static String doGet(String url) {
		return doGet(url, 20000);
	}
	
	public static String doGet(String url, String charset) {
		return doGet(url, 3000, 20000, charset);
	}
	
	
	public static String doGet(String url, int readTimeout) {
		return doGet(url, 3000, readTimeout);
	}
	
	
	public static String doGet(String url, int connTimeout, int readTimeout) {
		return doGet(url, connTimeout, readTimeout, null);
	}
	
	public static String doGet(String url, int connTimeout, int readTimeout, String charset) {
		HttpClient client = new HttpClient();  
		client.getHttpConnectionManager().getParams().setConnectionTimeout(connTimeout);
		client.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);
		client.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(100);
		client.getHttpConnectionManager().getParams().setMaxTotalConnections(500);
		String res = null;
        // Create a method instance.
        GetMethod method = null;
        try {  
        	method = new GetMethod(url);
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            // Execute the method.  
            int statusCode = client.executeMethod(method); 
            if (statusCode == HttpStatus.SC_OK) {
				BufferedReader reader = null;
				if (charset == null) {
					reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				} else {
					reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
				}
            	StringBuffer stringBuffer = new StringBuffer();  
            	String str = "";  
            	while((str = reader.readLine())!=null){  
            	    stringBuffer.append(str);  
            	} 
            	res = stringBuffer.toString();
            } else {  
            	logger.info("Response Code: " + statusCode);  
            }  
        } catch (Exception e) {  
            logger.error("url=" + url + "\r\n", e);
        } finally {  
            if(method != null) {
            	method.releaseConnection();
            } 
        }
        
        return res;
	}
	
	
}
