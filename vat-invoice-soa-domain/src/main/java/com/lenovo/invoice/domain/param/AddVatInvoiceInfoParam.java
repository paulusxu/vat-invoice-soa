package com.lenovo.invoice.domain.param;

import java.io.Serializable;

/**
 * Created by mayan3 on 2016/6/24.
 */
public class AddVatInvoiceInfoParam implements Serializable {
    private String lenovoId;
    private String customerName; //公司名
    private String taxNo;//税号
    private String bankName;//开户行
    private String accountNo;//开户账号
    private String address;//地址
    private String phoneNo;//电话
    private boolean isShard;//是否共享 true：共享 false:不共享
    private boolean isNeedMerge;//是否合并 可不填
    private int shopId;//
//    private int type; //0：官网增票 1：o2o增票
    private String faid;//faid
    private String faType;

    private String region;// think o2o专用

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public boolean isShard() {
        return isShard;
    }

    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean getIsShard() {
        return isShard;
    }

    public void setIsShard(boolean isShard) {
        this.isShard = isShard;
    }

    public boolean getIsNeedMerge() {
        return isNeedMerge;
    }

    public void setIsNeedMerge(boolean isNeedMerge) {
        this.isNeedMerge = isNeedMerge;
    }

    public boolean isNeedMerge() {
        return isNeedMerge;
    }

    public String getFaType() {
        return faType;
    }

    public void setFaType(String faType) {
        this.faType = faType;
    }

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
