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

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.OrderId
     *
     * @return the value of orderinvoice.OrderId
     * @mbggenerated
     */
    public Long getOrderid() {
        return orderid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.OrderId
     *
     * @param orderid the value for orderinvoice.OrderId
     * @mbggenerated
     */
    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Type
     *
     * @return the value of orderinvoice.Type
     * @mbggenerated
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Type
     *
     * @param type the value for orderinvoice.Type
     * @mbggenerated
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Title
     *
     * @return the value of orderinvoice.Title
     * @mbggenerated
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Title
     *
     * @param title the value for orderinvoice.Title
     * @mbggenerated
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Units
     *
     * @return the value of orderinvoice.Units
     * @mbggenerated
     */
    public String getUnits() {
        return units;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Units
     *
     * @param units the value for orderinvoice.Units
     * @mbggenerated
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.TaxpayerIdentity
     *
     * @return the value of orderinvoice.TaxpayerIdentity
     * @mbggenerated
     */
    public String getTaxpayeridentity() {
        return taxpayeridentity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.TaxpayerIdentity
     *
     * @param taxpayeridentity the value for orderinvoice.TaxpayerIdentity
     * @mbggenerated
     */
    public void setTaxpayeridentity(String taxpayeridentity) {
        this.taxpayeridentity = taxpayeridentity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.RegisterAddress
     *
     * @return the value of orderinvoice.RegisterAddress
     * @mbggenerated
     */
    public String getRegisteraddress() {
        return registeraddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.RegisterAddress
     *
     * @param registeraddress the value for orderinvoice.RegisterAddress
     * @mbggenerated
     */
    public void setRegisteraddress(String registeraddress) {
        this.registeraddress = registeraddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.RegisterPhone
     *
     * @return the value of orderinvoice.RegisterPhone
     * @mbggenerated
     */
    public String getRegisterphone() {
        return registerphone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.RegisterPhone
     *
     * @param registerphone the value for orderinvoice.RegisterPhone
     * @mbggenerated
     */
    public void setRegisterphone(String registerphone) {
        this.registerphone = registerphone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.DepositBank
     *
     * @return the value of orderinvoice.DepositBank
     * @mbggenerated
     */
    public String getDepositbank() {
        return depositbank;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.DepositBank
     *
     * @param depositbank the value for orderinvoice.DepositBank
     * @mbggenerated
     */
    public void setDepositbank(String depositbank) {
        this.depositbank = depositbank;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.BankNo
     *
     * @return the value of orderinvoice.BankNo
     * @mbggenerated
     */
    public String getBankno() {
        return bankno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.BankNo
     *
     * @param bankno the value for orderinvoice.BankNo
     * @mbggenerated
     */
    public void setBankno(String bankno) {
        this.bankno = bankno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.CreateTime
     *
     * @return the value of orderinvoice.CreateTime
     * @mbggenerated
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.CreateTime
     *
     * @param createtime the value for orderinvoice.CreateTime
     * @mbggenerated
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.UpdateTime
     *
     * @return the value of orderinvoice.UpdateTime
     * @mbggenerated
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.UpdateTime
     *
     * @param updatetime the value for orderinvoice.UpdateTime
     * @mbggenerated
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.IsNeedMerge
     *
     * @return the value of orderinvoice.IsNeedMerge
     * @mbggenerated
     */
    public Integer getIsneedmerge() {
        return isneedmerge;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.IsNeedMerge
     *
     * @param isneedmerge the value for orderinvoice.IsNeedMerge
     * @mbggenerated
     */
    public void setIsneedmerge(Integer isneedmerge) {
        this.isneedmerge = isneedmerge;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Zid
     *
     * @return the value of orderinvoice.Zid
     * @mbggenerated
     */
    public String getZid() {
        return zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Zid
     *
     * @param zid the value for orderinvoice.Zid
     * @mbggenerated
     */
    public void setZid(String zid) {
        this.zid = zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.PayName
     *
     * @return the value of orderinvoice.PayName
     * @mbggenerated
     */
    public String getPayname() {
        return payname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.PayName
     *
     * @param payname the value for orderinvoice.PayName
     * @mbggenerated
     */
    public void setPayname(String payname) {
        this.payname = payname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.IsConfirmPersonVat
     *
     * @return the value of orderinvoice.IsConfirmPersonVat
     * @mbggenerated
     */
    public Integer getIsconfirmpersonvat() {
        return isconfirmpersonvat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.IsConfirmPersonVat
     *
     * @param isconfirmpersonvat the value for orderinvoice.IsConfirmPersonVat
     * @mbggenerated
     */
    public void setIsconfirmpersonvat(Integer isconfirmpersonvat) {
        this.isconfirmpersonvat = isconfirmpersonvat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.ShopId
     *
     * @return the value of orderinvoice.ShopId
     * @mbggenerated
     */
    public Integer getShopid() {
        return shopid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.ShopId
     *
     * @param shopid the value for orderinvoice.ShopId
     * @mbggenerated
     */
    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.CurrencyCode
     *
     * @return the value of orderinvoice.CurrencyCode
     * @mbggenerated
     */
    public String getCurrencycode() {
        return currencycode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.CurrencyCode
     *
     * @param currencycode the value for orderinvoice.CurrencyCode
     * @mbggenerated
     */
    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.No
     *
     * @return the value of orderinvoice.No
     * @mbggenerated
     */
    public String getNo() {
        return no;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.No
     *
     * @param no the value for orderinvoice.No
     * @mbggenerated
     */
    public void setNo(String no) {
        this.no = no;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Code
     *
     * @return the value of orderinvoice.Code
     * @mbggenerated
     */
    public String getCode() {
        return code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Code
     *
     * @param code the value for orderinvoice.Code
     * @mbggenerated
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.amount
     *
     * @return the value of orderinvoice.amount
     * @mbggenerated
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.amount
     *
     * @param amount the value for orderinvoice.amount
     * @mbggenerated
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Date
     *
     * @return the value of orderinvoice.Date
     * @mbggenerated
     */
    public String getDate() {
        return date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Date
     *
     * @param date the value for orderinvoice.Date
     * @mbggenerated
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.BillNo
     *
     * @return the value of orderinvoice.BillNo
     * @mbggenerated
     */
    public String getBillno() {
        return billno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.BillNo
     *
     * @param billno the value for orderinvoice.BillNo
     * @mbggenerated
     */
    public void setBillno(String billno) {
        this.billno = billno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.BillDate
     *
     * @return the value of orderinvoice.BillDate
     * @mbggenerated
     */
    public String getBilldate() {
        return billdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.BillDate
     *
     * @param billdate the value for orderinvoice.BillDate
     * @mbggenerated
     */
    public void setBilldate(String billdate) {
        this.billdate = billdate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.BillCompany
     *
     * @return the value of orderinvoice.BillCompany
     * @mbggenerated
     */
    public String getBillcompany() {
        return billcompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.BillCompany
     *
     * @param billcompany the value for orderinvoice.BillCompany
     * @mbggenerated
     */
    public void setBillcompany(String billcompany) {
        this.billcompany = billcompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column orderinvoice.Url
     *
     * @return the value of orderinvoice.Url
     * @mbggenerated
     */
    public String getUrl() {
        return url;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column orderinvoice.Url
     *
     * @param url the value for orderinvoice.Url
     * @mbggenerated
     */
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