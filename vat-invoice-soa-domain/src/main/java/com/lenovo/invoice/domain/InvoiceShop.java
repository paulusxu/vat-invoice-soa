package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xuweihua on 2016/7/25.
 */
public class InvoiceShop  implements Serializable {
    private Integer id;
    private String  uuid;
    private String customerName;
    private String lenovoID;
    private Integer invoiceType;
    private Integer payManType;
    private String payMan;
    private String taxNo;
    private String taxNoType;//纳税人类型  0 ：税务登记，1:统一社会信用代码，2：无事业单位
    private Integer companyType;//开票公司类型
    private String bankName;
    private String accountNo;
    private String subAreaCode;
    private String subAreaName;
    private String zip;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String countyCode;
    private String countyName;
    private String address;
    private String phoneNo;
    private Integer isDefault;
    private Integer approvalStatus;
    private String approver;
    private Date approvalDate;
    private String soldToCode;
    private Integer isShow;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private Integer isConfirmPersonal;
    private String updateBy;
    private Integer synType;
    private Integer shopId;
    private boolean noLog;//true:不写日志  false:写日志

    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }

    public String getTaxNoType() {
        return taxNoType;
    }

    public void setTaxNoType(String taxNoType) {
        this.taxNoType = taxNoType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLenovoID() {
        return lenovoID;
    }

    public void setLenovoID(String lenovoID) {
        this.lenovoID = lenovoID;
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Integer getPayManType() {
        return payManType;
    }

    public void setPayManType(Integer payManType) {
        this.payManType = payManType;
    }

    public String getPayMan() {
        return payMan;
    }

    public void setPayMan(String payMan) {
        this.payMan = payMan;
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

    public String getSubAreaCode() {
        return subAreaCode;
    }

    public void setSubAreaCode(String subAreaCode) {
        this.subAreaCode = subAreaCode;
    }

    public String getSubAreaName() {
        return subAreaName;
    }

    public void setSubAreaName(String subAreaName) {
        this.subAreaName = subAreaName;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
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

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getSoldToCode() {
        return soldToCode;
    }

    public void setSoldToCode(String soldToCode) {
        this.soldToCode = soldToCode;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsConfirmPersonal() {
        return isConfirmPersonal;
    }

    public void setIsConfirmPersonal(Integer isConfirmPersonal) {
        this.isConfirmPersonal = isConfirmPersonal;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getSynType() {
        return synType;
    }

    public void setSynType(Integer synType) {
        this.synType = synType;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public boolean isNoLog() {
        return noLog;
    }

    public void setNoLog(boolean noLog) {
        this.noLog = noLog;
    }
}
