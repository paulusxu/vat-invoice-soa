package com.lenovo.invoice.domain;

import java.util.Date;

public class VathrowBtcp {
    private Integer id; //主键id
    private String title; //发票抬头
    private String taxpayeridentity;  //纳税人识别号
    private String depositbank; //开户行
    private String bankno; //银行账户
    private String registeraddress;  //注册地址
    private String registerphone;  //注册电话
    private String outid;
    private String membercode;  //用户账号
    private String name;  //收货人姓名
    private String provinceid;  //省市编号
    private String city;  //市名称
    private String county;  //区县名称
    private String address;  //详细地址
    private String phone;  //固定电话
    private String zip;  //邮编
    private int isneedmerge;  //是否合并开票
    private int throwingStatus; //是否抛单成功 0:未抛单 1:不允许抛单 2:已抛单 3：抛单成功 4:抛单失败
    private Date createtime;
    private String orderCode;  //订单号
    private String zid;  //增票id
    private int orderStatus;  //订单状态1:已支付 2:已抛单 3:已发货
    private String throwResult;  //抛送返回值

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaxpayeridentity() {
        return taxpayeridentity;
    }

    public void setTaxpayeridentity(String taxpayeridentity) {
        this.taxpayeridentity = taxpayeridentity;
    }

    public String getDepositbank() {
        return depositbank;
    }

    public void setDepositbank(String depositbank) {
        this.depositbank = depositbank;
    }

    public String getBankno() {
        return bankno;
    }

    public void setBankno(String bankno) {
        this.bankno = bankno;
    }

    public String getRegisteraddress() {
        return registeraddress;
    }

    public void setRegisteraddress(String registeraddress) {
        this.registeraddress = registeraddress;
    }

    public String getRegisterphone() {
        return registerphone;
    }

    public void setRegisterphone(String registerphone) {
        this.registerphone = registerphone;
    }

    public String getOutid() {
        return outid;
    }

    public void setOutid(String outid) {
        this.outid = outid;
    }

    public String getMembercode() {
        return membercode;
    }

    public void setMembercode(String membercode) {
        this.membercode = membercode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
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

    public int getIsneedmerge() {
        return isneedmerge;
    }

    public void setIsneedmerge(int isneedmerge) {
        this.isneedmerge = isneedmerge;
    }

    public int getThrowingStatus() {
        return throwingStatus;
    }

    public void setThrowingStatus(int throwingStatus) {
        this.throwingStatus = throwingStatus;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getThrowResult() {
        return throwResult;
    }

    public void setThrowResult(String throwResult) {
        this.throwResult = throwResult;
    }
}