package com.lenovo.invoice.domain.param;

import java.io.Serializable;

/**
 * Created by mayan3 on 2016/6/22.
 */
public class GetVatInvoiceInfoParam implements Serializable {
    private String lenovoId;
    private String customerName;
    private String taxNo;

    private int shopId;
//    private int type; //0：官网增票 1：o2o增票

    private String faid;
    private String region;

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }


    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }


//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
}
