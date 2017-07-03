package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/3/20.
 */
public class ExchangeInvoiceRecord implements Serializable {

    //换票记录信息
    private String id;//主键id，和调用BTCP接口的唯一标识一致
    private String itCode;//操作人itcode
    private String orderCode;//订单号
    private String BTCPOrderCode;//BTCP订单号
    private Integer shopid;//商城
    private Integer state;//换票的状态，1换票中，2换票成功，3换票失败
    private Date exchangeTime;//换票的时间
    private Date updateTime;//最近一次修改时间
    private Integer exchangeType;//换票类型，1电换普，2增换普、3普换普，4电换增、5增换增、6普换增
    private String lenovoId;
    private Date paidTime;

    //老发票信息
    private Integer oldType;//开票方式，1公司，0个人
    private Long oldInvoiceId;//发票的id
    private Integer oldInvoiceType;//发票的类型，1电，2增，3普
    private String oldInvoiceTitle;//发票的抬头
    private String oldTaxNo;//税号
    private Integer oldTaxNoType;//旧发票识别码类型，1是15、20位，2是18位，3是无
    private String oldBankName;//开户银行
    private String oldBankNo;//开户账号
    private String oldAddress;//开户地址
    private String oldPhone;//开户电话

    //新发票信息
    private Integer newType;//开票方式，1公司，0个人
    private Long newInvoiceId;//发票的id
    private Integer newInvoiceType;//发票的类型，1电，2增，3普
    private String newInvoiceTitle;//发票的抬头
    private String newTaxNo;//税号
    private Integer newTaxNoType;//新发票识别码类型，1是15、20位，2是18位，3是无
    private String newBankName;//开户银行
    private String newBankNo;//开户账号
    private String newAddress;//开户地址
    private String newPhone;//开户电话

    //收票地址
    private String name;//收票人姓名
    private String provinceId;//省份id
    private String province;//省份
    private String city;//城市
    private String county;//区县
    private String address;//详细地址
    private String phone;//收票人电话
    private String tel;//收票人固定电话
    private String zip;//邮编

    //分页查询，换票开始时间，结束时间
    private Date beginTime;
    private Date endTime;

    public Integer getOldTaxNoType() {
        return oldTaxNoType;
    }

    public void setOldTaxNoType(Integer oldTaxNoType) {
        this.oldTaxNoType = oldTaxNoType;
    }

    public Integer getNewTaxNoType() {
        return newTaxNoType;
    }

    public void setNewTaxNoType(Integer newTaxNoType) {
        this.newTaxNoType = newTaxNoType;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Date getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(Date paidTime) {
        this.paidTime = paidTime;
    }

    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getShopid() {
        return shopid;
    }

    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItCode() {
        return itCode;
    }

    public void setItCode(String itCode) {
        this.itCode = itCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getBTCPOrderCode() {
        return BTCPOrderCode;
    }

    public void setBTCPOrderCode(String BTCPOrderCode) {
        this.BTCPOrderCode = BTCPOrderCode;
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

    public Integer getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
    }

    public Integer getOldType() {
        return oldType;
    }

    public void setOldType(Integer oldType) {
        this.oldType = oldType;
    }

    public Integer getOldInvoiceType() {
        return oldInvoiceType;
    }

    public void setOldInvoiceType(Integer oldInvoiceType) {
        this.oldInvoiceType = oldInvoiceType;
    }

    public String getOldInvoiceTitle() {
        return oldInvoiceTitle;
    }

    public void setOldInvoiceTitle(String oldInvoiceTitle) {
        this.oldInvoiceTitle = oldInvoiceTitle;
    }

    public String getOldTaxNo() {
        return oldTaxNo;
    }

    public void setOldTaxNo(String oldTaxNo) {
        this.oldTaxNo = oldTaxNo;
    }

    public String getOldBankName() {
        return oldBankName;
    }

    public void setOldBankName(String oldBankName) {
        this.oldBankName = oldBankName;
    }

    public String getOldBankNo() {
        return oldBankNo;
    }

    public void setOldBankNo(String oldBankNo) {
        this.oldBankNo = oldBankNo;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }

    public String getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
    }

    public Integer getNewType() {
        return newType;
    }

    public void setNewType(Integer newType) {
        this.newType = newType;
    }

    public Integer getNewInvoiceType() {
        return newInvoiceType;
    }

    public void setNewInvoiceType(Integer newInvoiceType) {
        this.newInvoiceType = newInvoiceType;
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

    public String getNewBankName() {
        return newBankName;
    }

    public void setNewBankName(String newBankName) {
        this.newBankName = newBankName;
    }

    public String getNewBankNo() {
        return newBankNo;
    }

    public void setNewBankNo(String newBankNo) {
        this.newBankNo = newBankNo;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }

    public Long getNewInvoiceId() {
        return newInvoiceId;
    }

    public void setNewInvoiceId(Long newInvoiceId) {
        this.newInvoiceId = newInvoiceId;
    }

    public Long getOldInvoiceId() {
        return oldInvoiceId;
    }

    public void setOldInvoiceId(Long oldInvoiceId) {
        this.oldInvoiceId = oldInvoiceId;
    }
}
