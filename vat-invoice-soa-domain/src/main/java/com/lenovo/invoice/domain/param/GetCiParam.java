package com.lenovo.invoice.domain.param;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xuweihua on 2017/4/26.
 */
public class GetCiParam  implements Serializable {
    private int shopId;
    private int salesType;
    private int bu;
    private boolean silenceOrder;
    private BigDecimal bigDecimal;
    private List<FaData> faDatas;

    public boolean isSilenceOrder() {
        return silenceOrder;
    }

    public void setSilenceOrder(boolean silenceOrder) {
        this.silenceOrder = silenceOrder;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

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

