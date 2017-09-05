package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/3/16.
 * 普通发票  已废除
 */
public class CommonInvoice implements Serializable{

    private Integer id;//主键id
    private String invoiceTitle;//发票抬头
    private Integer shopid;//商城
    private Integer type;//开票方式，1是公司，2是个人
    private String taxNo;//公司税号
    private Date createtime;//创建时间
    private String createBy;//创建人

    private String lenovoId;//用户id
    private Integer mappingId;//映射表id

    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public Integer getMappingId() {
        return mappingId;
    }

    public void setMappingId(Integer mappingId) {
        this.mappingId = mappingId;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
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
