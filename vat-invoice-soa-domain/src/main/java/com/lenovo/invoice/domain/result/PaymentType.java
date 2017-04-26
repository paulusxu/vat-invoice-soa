package com.lenovo.invoice.domain.result;

/**
 * Created by xuweihua on 2017/4/26.
 */
public enum PaymentType {
    HDFK(0,"货到付款"),
    ZXZF(1,"在线支付"),
    XXZZ(2,"线下银行转账");

    private PaymentType(int type, String descr) {
        this.type = type;
        this.descr = descr;
    }
    private int type;
    private String descr;

    public int getType() {
        return type;
    }

    public String getDescr() {
        return descr;
    }
}
