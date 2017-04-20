package com.lenovo.invoice.domain;

import com.lenovo.enums.ENUM;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by xuweihua on 2017/4/10.
 */
public enum InvoiceType {
    DZFP(0,"电子发票"),
    PTFP(1,"普通发票"),
    ZZFP(2,"增值发票");


    private InvoiceType(int type, String descr) {
        this.type = type;
        this.descr = descr;
    }
    private final int type;
    private final String descr;

    public int getType() {
        return type;
    }

    public String getDescr() {
        return descr;
    }

}
