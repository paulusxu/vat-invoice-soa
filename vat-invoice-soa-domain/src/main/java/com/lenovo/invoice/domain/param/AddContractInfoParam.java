package com.lenovo.invoice.domain.param;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mayan3 on 2016/8/12.
 */
public class AddContractInfoParam implements Serializable{
    private String contractNo;
    private String buyerName;
    private String shipName;
    private String shipMobile;
    private String shipAddress;
    private Date takeffectTime;
    private String lenovoId;
    private Boolean issend;
    private String orderCode;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public Date getTakeffectTime() {
        return takeffectTime;
    }

    public void setTakeffectTime(Date takeffectTime) {
        this.takeffectTime = takeffectTime;
    }

    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public Boolean getIssend() {
        return issend;
    }

    public void setIssend(Boolean issend) {
        this.issend = issend;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
