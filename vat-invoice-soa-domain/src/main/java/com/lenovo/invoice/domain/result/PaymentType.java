package com.lenovo.invoice.domain.result;

/**
 * Created by xuweihua on 2017/4/26.
 */
public enum PaymentType {
    ZXZF(0,"在线支付"),
    HDFK(1,"货到付款"),
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
