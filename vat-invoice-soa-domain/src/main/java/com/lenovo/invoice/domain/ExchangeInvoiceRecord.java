package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/3/20.
 */
public class ExchangeInvoiceRecord implements Serializable {

    private String id;//主键id，和调用BTCP接口的唯一标识一致
    private String itCode;//操作人itcode
    private String orderCode;//订单号
    private Integer oldInvoiceId;//老发票的id
    private Integer oldInvoiceType;//老发票的类型
    private String oldInvoiceTitle;//老发票的抬头
    private Integer newInvoiceId;//新发票的id
    private Integer newInvoiceType;//新发票的类型
    private String newInvoiceTitle;//新发票的抬头
    private String taxNo;//税号
    private String bankName;//开户银行
    private String bankNo;//开户账号
    private String address;//开户地址
    private String phone;//开户电话
    private Integer state;//换票的状态，1换票中，2换票成功，3换票失败
    private Date exchangeTime;//换票的时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNewInvoiceId() {
        return newInvoiceId;
    }

    public void setNewInvoiceId(Integer newInvoiceId) {
        this.newInvoiceId = newInvoiceId;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(Date exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    public Integer getOldInvoiceId() {
        return oldInvoiceId;
    }

    public void setOldInvoiceId(Integer oldInvoiceId) {
        this.oldInvoiceId = oldInvoiceId;
    }

    public String getOldInvoiceTitle() {
        return oldInvoiceTitle;
    }

    public void setOldInvoiceTitle(String oldInvoiceTitle) {
        this.oldInvoiceTitle = oldInvoiceTitle;
    }

    public String getNewInvoiceTitle() {
        return newInvoiceTitle;
    }

    public void setNewInvoiceTitle(String newInvoiceTitle) {
        this.newInvoiceTitle = newInvoiceTitle;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getOldInvoiceType() {
        return oldInvoiceType;
    }

    public void setOldInvoiceType(Integer oldInvoiceType) {
        this.oldInvoiceType = oldInvoiceType;
    }

    public Integer getNewInvoiceType() {
        return newInvoiceType;
    }

    public void setNewInvoiceType(Integer newInvoiceType) {
        this.newInvoiceType = newInvoiceType;
    }

    public String getItCode() {
        return itCode;
    }

    public void setItCode(String itCode) {
        this.itCode = itCode;
    }
}
