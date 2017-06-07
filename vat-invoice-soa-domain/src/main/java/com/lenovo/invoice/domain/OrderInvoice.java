package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderInvoice implements Serializable {

    private Long orderid;
    private Integer type; //0：电子票 1：普票 2：增票
    private String title;//发票抬头
    private String units;//0：个人 1：公司
    private String taxpayeridentity;//增票税号
    private String registeraddress;//增票注册地址
    private String registerphone;//增票电话
    private String depositbank;//增票开户行
    private String bankno;//增票开户行账号
    private Date createtime;
    private Date updatetime;
    private Integer isneedmerge;//是否合并开票
    private String zid;
    private String payname;//smb付款方名称
    private Integer isconfirmpersonvat;//smb是否有个人承诺0否 1是
    private Integer shopid;
    private String currencycode;
    private String no;//发票代码
    private String code;//发票号码
    private BigDecimal amount;//发票金额
    private String date;//开票日期
    private String billno;//快递单号
    private String billdate;//发票邮寄日期
    private String billcompany;//快递公司
    private String url;//电子票URL
    private int flag;//0:不可修改 1：可修改
    private int isSignUp;//0：未签收 1：已签收

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getTaxpayeridentity() {
        return taxpayeridentity;
    }

    public void setTaxpayeridentity(String taxpayeridentity) {
        this.taxpayeridentity = taxpayeridentity;
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

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getIsneedmerge() {
        return isneedmerge;
    }

    public void setIsneedmerge(Integer isneedmerge) {
        this.isneedmerge = isneedmerge;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getPayname() {
        return payname;
    }

    public void setPayname(String payname) {
        this.payname = payname;
    }

    public Integer getIsconfirmpersonvat() {
        return isconfirmpersonvat;
    }

    public void setIsconfirmpersonvat(Integer isconfirmpersonvat) {
        this.isconfirmpersonvat = isconfirmpersonvat;
    }

    public Integer getShopid() {
        return shopid;
    }

    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getBilldate() {
        return billdate;
    }

    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    public String getBillcompany() {
        return billcompany;
    }

    public void setBillcompany(String billcompany) {
        this.billcompany = billcompany;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getIsSignUp() {
        return isSignUp;
    }

    public void setIsSignUp(int isSignUp) {
        this.isSignUp = isSignUp;
    }
}