package com.lenovo.invoice.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lenovo.leconf.client.common.annotations.LeconfFile;
import com.lenovo.leconf.client.common.annotations.LeconfFileItem;
import com.lenovo.leconf.client.common.annotations.LeconfUpdateService;
import com.lenovo.leconf.client.common.update.ILeconfUpdate;


@Component
@LeconfFile(filename = "other.properties")
@LeconfUpdateService(classes = { PropertiesConfig.class })
public class PropertiesConfig implements ILeconfUpdate{
	@Value(value = "on")
	private String openO2O = "on";
	@Value(value = "on")
	private String openZy = "on";

	@Value(value = "on")
	private String openDz = "on";

	@Value(value = "off")
	private String huiShangZF = "off";

	@Value(value = "")
	private String interfaceUrl="";
	@Value(value = "")
	private String appKey="";
	@Value(value = "")
	private String appSecret="";
	@Value(value = "")
	private String method="";
	@Value(value = "")
	private String sts="";

	@Value(value = "0,3")
	private String zy="0,3";

	@Value(value = "1,2,4")
	private String noZy="1,2,4";

	@LeconfFileItem(name = "zy")
	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	@LeconfFileItem(name = "noZy")
	public String getNoZy() {
		return noZy;
	}

	public void setNoZy(String noZy) {
		this.noZy = noZy;
	}

	@Value(value = "")
	private String smbUrl="";
	@LeconfFileItem(name = "smbUrl")
	public String getSmbUrl() {
		return smbUrl;
	}

	public void setSmbUrl(String smbUrl) {
		this.smbUrl = smbUrl;
	}

	@LeconfFileItem(name = "interfaceUrl")
	public String getInterfaceUrl() {
		return interfaceUrl;
	}

	public void setInterfaceUrl(String interfaceUrl) {
		this.interfaceUrl = interfaceUrl;
	}
	@LeconfFileItem(name = "appKey")
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	@LeconfFileItem(name = "appSecret")
	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	@LeconfFileItem(name = "method")
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	@LeconfFileItem(name = "sts")
	public String getSts() {
		return sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	@LeconfFileItem(name = "openDz")
	public String getOpenDz() {
		return openDz;
	}

	public void setOpenDz(String openDz) {
		this.openDz = openDz;
	}

	@LeconfFileItem(name = "huiShangZF")
	public String getHuiShangZF() {
		return huiShangZF;
	}
	public void setHuiShangZF(String huiShangZF) {
		this.huiShangZF = huiShangZF;
	}

	@LeconfFileItem(name = "openO2O")
	public String getOpenO2O() {
		return openO2O;
	}

	public void setOpenO2O(String openO2O) {
		this.openO2O = openO2O;
	}

	@LeconfFileItem(name = "openZy")
	public String getOpenZy() {
		return openZy;
	}

	public void setOpenZy(String openZy) {
		this.openZy = openZy;
	}

	@Override
	public void reload() throws Exception {

	}
}

