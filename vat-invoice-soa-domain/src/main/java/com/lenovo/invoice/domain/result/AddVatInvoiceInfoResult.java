package com.lenovo.invoice.domain.result;

import java.io.Serializable;

/**
 * Created by mayan3 on 2016/6/24.
 */
public class AddVatInvoiceInfoResult implements Serializable {
    private long vatInvoiceId;
    private String lenovoId;
    private String customerName;
    private String taxNo;
    private String bankName;
    private String accountNo;
    private String address;
    private String phoneNo;
    private Integer isCheck;
    private Boolean isShared;
    private Boolean IsNeedMerge;
    private int shopId;
    private String faid;
    private String storeId;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getVatInvoiceId() {
        return vatInvoiceId;
    }

    public void setVatInvoiceId(long vatInvoiceId) {
        this.vatInvoiceId = vatInvoiceId;
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

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

    public Boolean getIsShared() {
        return isShared;
    }

    public void setIsShared(Boolean isShared) {
        this.isShared = isShared;
    }

    public Boolean getIsNeedMerge() {
        return IsNeedMerge;
    }

    public void setIsNeedMerge(Boolean isNeedMerge) {
        IsNeedMerge = isNeedMerge;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
