package com.lenovo.invoice.domain;

import java.io.Serializable;

/**
 * Created by xuweihua on 2016/7/27.
 */
public class InvoiceIdAndUuid  implements Serializable {
    private Integer id;
    private String uuid;

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
}
