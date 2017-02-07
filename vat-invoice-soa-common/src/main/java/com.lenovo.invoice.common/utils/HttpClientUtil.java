package com.lenovo.invoice.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by mayan3 on 2015/10/8.
 */
public class HttpClientUtil {

	private static Logger logger = Logger.getLogger(HttpClientUtil.class);

	/**
	 * @description 发送httpClient post 请求，json 格式返回
	 * @author qinhc
	 * @2015下午6:03:09
	 * @param url
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String executeHttpPost(String url, String body)
			throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost method = new HttpPost(url);
		StringEntity entity = new StringEntity(body, "utf-8");// 解决中文乱码问题
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		method.setEntity(entity);
		String resData = "";
		// 请求超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				20000);
		try {
			HttpResponse result = httpClient.execute(method);
			// 请求结束，返回结果
			resData = EntityUtils.toString(result.getEntity());
			logger.info("订单消息返回的数据：" + resData);
		} catch (Exception e) {
			logger.error("订单消息发送请求出错：" + e.getMessage());
		} finally {
			method.releaseConnection();
		}
		return resData;
	}
	
	/**
	 * @description
	 * @author qinhc
	 * @2015下午6:00:41
	 * @param url
	 * @param body
	 * @param type 请求的类型
	 * @return
	 * @throws Exception
	 */
	public static String executeHttpPostType(String url, String body,String type)
			throws Exception {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost method = new HttpPost(url);
		StringEntity entity = new StringEntity(body, "utf-8");// 解决中文乱码问题
		entity.setContentEncoding("UTF-8");
		entity.setContentType(type);
		method.setEntity(entity);
		String resData = "";
		// 请求超时
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		// 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				20000);
		try {
			HttpResponse result = httpClient.execute(method);
			// 请求结束，返回结果
			resData = EntityUtils.toString(result.getEntity());
			logger.info("订单消息返回的数据：" + resData);
		} catch (Exception e) {
			logger.error("订单消息发送请求出错：" + e.getMessage());
		} finally {
			method.releaseConnection();
		}
		return resData;
	}
	
	/**
	 * 
	* @Description: post 提交
	* @author yuzj7@lenovo.com  
	* @date 2015年5月15日 上午10:41:49
	* @param url
	* @param params
	* @return
	 */
	public static String postStr(String url, Map<String, String> params) throws UnsupportedEncodingException{
		HttpClient httpclient =new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/xml; charset=utf-8");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		String line = null;   
		String str="";
		 try { 
			HttpResponse response=httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			InputStreamReader inputstream = new InputStreamReader(entity.getContent(), "UTF-8");
			BufferedReader reader = new BufferedReader(inputstream);   
			// 显示结果   
			while ((line = reader.readLine()) != null) {   
				str+=line;   
			} 
			reader.close();
			inputstream.close();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 if(httpclient != null){
				 httpclient.getConnectionManager().shutdown();
			 }
		 }
		return str;
	}
	
	
	/**
	 * post 方法
	 * @param url
	 * @return
	 * @author mamj
	 * @throws UnsupportedEncodingException
	 * @date 2013-11-19 下午03:46:58
	 */
	public static String PostStr(String url, Map<String, String> params,String referer,String cookie,boolean isproxy,String charset) throws UnsupportedEncodingException{
		HttpClient httpclient =new DefaultHttpClient();
		// 请求超时
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        // 读取超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000    );
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Entry<String, String> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		if(!StringUtils.isEmpty(cookie)){
			httpPost.setHeader("Cookie", cookie);
		}
		if(!StringUtils.isEmpty(referer)){
			httpPost.addHeader("Referer", referer);
		}
         
         if(StringUtils.isEmpty(charset)){
        	 charset = "utf-8";
         }
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
		String line = null;   
		 StringBuffer str = new StringBuffer("");  
		 InputStreamReader inreader = null;
		 BufferedReader reader = null;
		 try { 
			HttpResponse response=httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			inreader = new InputStreamReader(entity.getContent(), charset);
			reader  = new BufferedReader(inreader);   
			// 显示结果   
			while ((line = reader.readLine()) != null) {   
				str.append(line);
			} 
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 if (reader != null) {
	        	  try {
	        		  reader.close();// 最后要关闭BufferedReader  
				} catch (IOException e) {
					e.printStackTrace();
				}
	          }
	    	  if(inreader != null){
	    		  try {
	    			  inreader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
			 if(httpclient != null){
				 httpclient.getConnectionManager().shutdown();
			 }
		 }
		return str.toString();
	}
	
	
	public static String getStr(String url) {
		
		  BufferedReader in = null;  
	      // 定义HttpClient  
	      HttpClient client = new DefaultHttpClient();
	      // 实例化HTTP方法  
	      HttpGet request = new HttpGet();
	      String line = "";  
	      String tmp="";
	      try {  
		      request.setURI(new URI(url));  
		      HttpResponse response = client.execute(request);
	          in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));   
	          while ((tmp=in.readLine()) != null) {   
	            line+=tmp;   
	          }
	      }catch (Exception e) {  
	    	  e.printStackTrace();  
	      }finally{
	    	  if (in != null) {
	        	  try {
					in.close();// 最后要关闭BufferedReader  
				} catch (IOException e) {
					e.printStackTrace();
				}
	          }
	    	  client.getConnectionManager().shutdown();
	      }  
	      return line;  
	            
	}
}
