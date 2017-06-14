package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/6/14.
 * 废弃发票和目标发票的映射
 */
public class InvoiceToInvoice implements Serializable {
    private Long id; //废弃发票id
    private Long targetId; //目标发票id
    private Date createTime; //创建时间
    private String createBy; //创建人

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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
}
