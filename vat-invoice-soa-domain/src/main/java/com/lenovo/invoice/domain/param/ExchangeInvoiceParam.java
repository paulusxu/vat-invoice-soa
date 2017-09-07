package com.lenovo.invoice.domain.param;

import java.io.Serializable;

/**
 * Created by admin on 2017/9/4.
 */
public class ExchangeInvoiceParam implements Serializable{

    //订单号
    private String orderCode;
    //操作人code
    private String itCode;
    //开票方式，0个人，1公司
    private Integer type;
    //老发票类型
    private Integer oldInvoiceType;
    //换票类型
    private Integer exchangeType;
    //新发票信息-------------------
    //新发票抬头
    private String newInvoiceTitle;
    //新发票税号
    private String newTaxNo;
    //新发票 识别码类型
    private Integer taxNoType;
    //新发票开户银行名称
    private String newRegisterBankName;
    //新发票银行账号
    private String newRegisterBankNo;
    //新发票注册地址
    private String newRegisterAddress;
    //新发票注册电话
    private String newRegisterPhone;
    //收票地址------------------
    //新发票 收票人姓名
    private String name;
    //新发票 省名称
    private String province;
    //新发票 省编码
    private String provinceNo;
    //新发票 市名称
    private String city;
    //新发票 市编码
    private String cityNo;
    //新发票 区县名称
    private String county;
    //新发票 区县编码
    private String countyNo;
    //新发票 详细地址
    private String address;
    //新发票 收票人手机号
    private String mobile;
    //新发票 邮编
    private String zip;
    //新发票 收票人固定电话
    private String tel;

    public Integer getTaxNoType() {
        return taxNoType;
    }

    public void setTaxNoType(Integer taxNoType) {
        this.taxNoType = taxNoType;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getItCode() {
        return itCode;
    }

    public void setItCode(String itCode) {
        this.itCode = itCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOldInvoiceType() {
        return oldInvoiceType;
    }

    public void setOldInvoiceType(Integer oldInvoiceType) {
        this.oldInvoiceType = oldInvoiceType;
    }

    public Integer getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getNewInvoiceTitle() {
        return newInvoiceTitle;
    }

    public void setNewInvoiceTitle(String newInvoiceTitle) {
        this.newInvoiceTitle = newInvoiceTitle;
    }

    public String getNewTaxNo() {
        return newTaxNo;
    }

    public void setNewTaxNo(String newTaxNo) {
        this.newTaxNo = newTaxNo;
    }

    public String getNewRegisterBankName() {
        return newRegisterBankName;
    }

    public void setNewRegisterBankName(String newRegisterBankName) {
        this.newRegisterBankName = newRegisterBankName;
    }

    public String getNewRegisterBankNo() {
        return newRegisterBankNo;
    }

    public void setNewRegisterBankNo(String newRegisterBankNo) {
        this.newRegisterBankNo = newRegisterBankNo;
    }

    public String getNewRegisterAddress() {
        return newRegisterAddress;
    }

    public void setNewRegisterAddress(String newRegisterAddress) {
        this.newRegisterAddress = newRegisterAddress;
    }

    public String getNewRegisterPhone() {
        return newRegisterPhone;
    }

    public void setNewRegisterPhone(String newRegisterPhone) {
        this.newRegisterPhone = newRegisterPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceNo() {
        return provinceNo;
    }

    public void setProvinceNo(String provinceNo) {
        this.provinceNo = provinceNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyNo() {
        return countyNo;
    }

    public void setCountyNo(String countyNo) {
        this.countyNo = countyNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "ExchangeInvoiceParam{" +
                "orderCode='" + orderCode + '\'' +
                ", itCode='" + itCode + '\'' +
                ", type=" + type +
                ", oldInvoiceType=" + oldInvoiceType +
                ", exchangeType=" + exchangeType +
                ", newInvoiceTitle='" + newInvoiceTitle + '\'' +
                ", newTaxNo='" + newTaxNo + '\'' +
                ", taxNoType=" + taxNoType +
                ", newRegisterBankName='" + newRegisterBankName + '\'' +
                ", newRegisterBankNo='" + newRegisterBankNo + '\'' +
                ", newRegisterAddress='" + newRegisterAddress + '\'' +
                ", newRegisterPhone='" + newRegisterPhone + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", provinceNo='" + provinceNo + '\'' +
                ", city='" + city + '\'' +
                ", cityNo='" + cityNo + '\'' +
                ", county='" + county + '\'' +
                ", countyNo='" + countyNo + '\'' +
                ", address='" + address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", zip='" + zip + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
