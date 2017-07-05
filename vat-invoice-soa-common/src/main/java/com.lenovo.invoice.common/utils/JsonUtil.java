package com.lenovo.invoice.common.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

	private final static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	public static String toJsonString(Object obj) {
		if(obj == null) {
			return "";
		}
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("转Json出错。。。。。。");
			logger.error("error msg:", e);
			return null;
		}
	}
}
