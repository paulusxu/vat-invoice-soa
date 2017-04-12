package com.lenovo.invoice.domain.param;

import java.io.Serializable;

/**
 * Created by xuweihua on 2017/4/12.
 */
public class GetInvoiceTypeParam implements Serializable {
    private int shopId;
    private int salesType;
    private int fatype;
    private String faid;

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getSalesType() {
        return salesType;
    }

    public void setSalesType(int salesType) {
        this.salesType = salesType;
    }

    public int getFatype() {
        return fatype;
    }

    public void setFatype(int fatype) {
        this.fatype = fatype;
    }

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }
}
