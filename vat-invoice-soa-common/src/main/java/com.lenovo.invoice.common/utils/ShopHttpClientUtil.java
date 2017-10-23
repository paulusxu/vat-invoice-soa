package com.lenovo.invoice.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpConstants;

import java.io.*;
import java.net.URLDecoder;


public class ShopHttpClientUtil {

	private static Logger logger = Logger.getLogger(ShopHttpClientUtil.class);

	public static String sendPost(String requestUrl,String params){
		try {
			byte[] requestBytes = params.getBytes("utf-8"); // 将参数转为二进制流
			HttpClient httpClient = new HttpClient();// 客户端实例化
			PostMethod postMethod = new PostMethod(requestUrl);
			//设置请求头
			postMethod.setRequestHeader("MSP-AppKey", "8503DF4DD58E4F46886002C89F9D843E");
			postMethod.setRequestHeader("MSP-AuthKey", AuthKeyGen.getAuthKey("8C01DFF16C904D7AA04E698468CD22F0","4FCCD290E58548EDA7C786342AE50D99"));
			// 设置请求头  Content-Type
			postMethod.setRequestHeader("Content-Type", "application/json");
			InputStream inputStream = new ByteArrayInputStream(requestBytes, 0,
					requestBytes.length);
			RequestEntity requestEntity = new InputStreamRequestEntity(inputStream,
					requestBytes.length, "application/json; charset=utf-8"); // 请求体
			postMethod.setRequestEntity(requestEntity);
			httpClient.executeMethod(postMethod);// 执行请求
			InputStream soapResponseStream = postMethod.getResponseBodyAsStream();// 获取返回的流
			byte[] datas = readInputStream(soapResponseStream);// 从输入流中读取数据
			String result = new String(datas, "UTF-8");// 将二进制流转为String
			// 打印返回结果
			logger.info(result);

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}

	public static JSONObject sendGet(String url){
		//get请求返回结果
		JSONObject jsonResult = null;
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			//发送get请求
			HttpGet request = new HttpGet(url);
			request.setHeader("MSP-AppKey", "8503DF4DD58E4F46886002C89F9D843E");
			request.setHeader("MSP-AuthKey", AuthKeyGen.getAuthKey("8C01DFF16C904D7AA04E698468CD22F0", "4FCCD290E58548EDA7C786342AE50D99"));
			HttpResponse response = client.execute(request);

			/**请求发送成功，并得到响应**/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				/**读取服务器返回过来的json字符串数据**/
				String strResult = EntityUtils.toString(response.getEntity(),"UTF-8");
				/**把json字符串转换成json对象**/
				jsonResult = JSONObject.parseObject(strResult);
			} else {
				logger.error("get请求提交失败:" + url+">>"+ EntityUtils.toString(response.getEntity(),"UTF-8"));
			}
		} catch (IOException e) {
			logger.error("get请求提交失败:" + url, e);
		}
		return jsonResult;
	}


	public static String sendPut(String requestUrl,String params){
		try {
			byte[] requestBytes = params.getBytes("utf-8"); // 将参数转为二进制流
			HttpClient httpClient = new HttpClient();// 客户端实例化
			PutMethod  putMethod = new PutMethod (requestUrl);
			//设置请求头
			putMethod.setRequestHeader("MSP-AppKey", "8503DF4DD58E4F46886002C89F9D843E");
			putMethod.setRequestHeader("MSP-AuthKey", AuthKeyGen.getAuthKey("8C01DFF16C904D7AA04E698468CD22F0","4FCCD290E58548EDA7C786342AE50D99"));
			// 设置请求头  Content-Type
			putMethod.setRequestHeader("Content-Type", "application/json");
			InputStream inputStream = new ByteArrayInputStream(requestBytes, 0,
					requestBytes.length);
			RequestEntity requestEntity = new InputStreamRequestEntity(inputStream,
					requestBytes.length, "application/json; charset=utf-8"); // 请求体
			putMethod.setRequestEntity(requestEntity);
			httpClient.executeMethod(putMethod);// 执行请求
			InputStream soapResponseStream = putMethod.getResponseBodyAsStream();// 获取返回的流
			byte[] datas = readInputStream(soapResponseStream);// 从输入流中读取数据
			String result = new String(datas, "UTF-8");// 将二进制流转为String
			// 打印返回结果
			logger.info(result);

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static String sendDelete(String requestUrl,String params){
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpDeleteWithBody method = new HttpDeleteWithBody(requestUrl);
			method.setHeader("MSP-AppKey", "8503DF4DD58E4F46886002C89F9D843E");
			method.setHeader("MSP-AuthKey", AuthKeyGen.getAuthKey("8C01DFF16C904D7AA04E698468CD22F0", "4FCCD290E58548EDA7C786342AE50D99"));
			StringEntity entity = new StringEntity(params, "utf-8");// 解决中文乱码问题
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
				logger.info("返回的数据：" + resData);
			} catch (Exception e) {
				logger.error("发送请求出错：" + e.getMessage());
			} finally {
				method.releaseConnection();
			}
			return resData;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

}
