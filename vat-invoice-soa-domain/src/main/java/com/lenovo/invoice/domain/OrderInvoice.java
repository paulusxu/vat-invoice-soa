package com.lenovo.invoice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lenovo.m2.arch.framework.domain.Money;
import com.lenovo.m2.arch.framework.domain.Tenant;

import java.io.Serializable;
import java.util.Date;

public class OrderInvoice implements Serializable {
    /**
     * 增值发票审核状态-1:审批通过
     */
    public static final int REVIEW_STATUS_ACCEPT = 1;
    /**
     * 增值发票审核状态-2:审批驳回
     */
    public static final int REVIEW_STATUS_REJECTED = 2;

    /**
     * 增值发票审核状态 1--未审批
     */
    public final static int REVIEW_STATUS_UNAUDITED = 3;


    private Long orderId;

    private Integer type;

    private String category;

    private String title;

    private String taxpayerIdentity;

    private String registerAddress;

    private String registerPhone;

    private String depositBank;

    private String bankNo;

    private String pictureUrl;

    private Integer isNeedMerge;

    private Integer reviewStatus;

    private Integer throwStatus;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date throwTime;

    private Integer throwTimes;

    private String failureReason;

    private String zCode;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;
    private String updateBy;
    private Integer version;
    private String zid;
    private String no;// 发票代码
    private String code;// 发票号码
    private Money amount;// 发票金额
    private String date;// 开票日期
    private String billNo;// 发票快递单号
    private String billDate;// 发票邮寄日期
    private String billCompany;// 快递公司

    private  String url; //电子票url地址

    private Tenant tenant;

    private Integer units;//0 个人，1公司

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getTaxpayerIdentity() {
        return taxpayerIdentity;
    }

    public void setTaxpayerIdentity(String taxpayerIdentity) {
        this.taxpayerIdentity = taxpayerIdentity == null ? null : taxpayerIdentity.trim();
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress == null ? null : registerAddress.trim();
    }

    public String getRegisterPhone() {
        return registerPhone;
    }

    public void setRegisterPhone(String registerPhone) {
        this.registerPhone = registerPhone == null ? null : registerPhone.trim();
    }

    public String getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(String depositBank) {
        this.depositBank = depositBank == null ? null : depositBank.trim();
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo == null ? null : bankNo.trim();
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl == null ? null : pictureUrl.trim();
    }

    public Integer getIsNeedMerge() {
        return isNeedMerge;
    }

    public void setIsNeedMerge(Integer isNeedMerge) {
        this.isNeedMerge = isNeedMerge;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public Integer getThrowStatus() {
        return throwStatus;
    }

    public void setThrowStatus(Integer throwStatus) {
        this.throwStatus = throwStatus;
    }

    public Date getThrowTime() {
        return throwTime;
    }

    public void setThrowTime(Date throwTime) {
        this.throwTime = throwTime;
    }

    public Integer getThrowTimes() {
        return throwTimes;
    }

    public void setThrowTimes(Integer throwTimes) {
        this.throwTimes = throwTimes;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason == null ? null : failureReason.trim();
    }

    public String getZCode() {
        return zCode;
    }

    public void setZCode(String zCode) {
        this.zCode = zCode;
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
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getzCode() {
        return zCode;
    }
    public void setzCode(String zCode) {
        this.zCode = zCode;
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

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillCompany() {
        return billCompany;
    }

    public void setBillCompany(String billCompany) {
        this.billCompany = billCompany;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "orderId=" + orderId +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", taxpayerIdentity='" + taxpayerIdentity + '\'' +
                ", registerAddress='" + registerAddress + '\'' +
                ", registerPhone='" + registerPhone + '\'' +
                ", depositBank='" + depositBank + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", reviewStatus=" + reviewStatus +
                ", throwStatus=" + throwStatus +
                ", throwTime=" + throwTime +
                ", throwTimes=" + throwTimes +
                ", failureReason='" + failureReason + '\'' +
                ", zCode='" + zCode + '\'' +
                ", zid='" + zid + '\'' +
                '}';
    }
}