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

	@Value(value = "off")
	private String huiShangZF = "off";


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

