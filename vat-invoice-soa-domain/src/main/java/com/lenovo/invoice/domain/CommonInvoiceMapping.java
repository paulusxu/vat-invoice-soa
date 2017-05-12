package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/3/16.
 * 普通发票映射用户信息
 */
public class CommonInvoiceMapping implements Serializable{

    private Integer id;//主键id
    private String lenovoId;//用户id
    private Integer commonInvoiceId;//外键，指向普通发票的主键
    private Integer shopid;//商城
    private Integer type;//开票方式，1公司，0个人
    private Date createtime;//创建时间
    private String createBy;//创建人

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCommonInvoiceId() {
        return commonInvoiceId;
    }

    public void setCommonInvoiceId(Integer commonInvoiceId) {
        this.commonInvoiceId = commonInvoiceId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public Integer getShopid() {
        return shopid;
    }

    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
