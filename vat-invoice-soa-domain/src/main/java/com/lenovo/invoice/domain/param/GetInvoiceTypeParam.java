package com.lenovo.invoice.domain.param;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuweihua on 2017/4/12.
 */
public class GetInvoiceTypeParam implements Serializable {
    private int shopId;
    private int salesType;
    private int bu;
    private List<FaData> faDatas;

    public int getBu() {
        return bu;
    }

    public void setBu(int bu) {
        this.bu = bu;
    }

    public List<FaData> getFaDatas() {
        return faDatas;
    }

    public void setFaDatas(List<FaData> faDatas) {
        this.faDatas = faDatas;
    }

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

}
