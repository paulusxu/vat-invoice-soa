package com.lenovo.invoice.domain.result;

/**
 * Created by xuweihua on 2017/7/26.
 */
public enum DeliverGoodsTypeEnum {
    AIRE_MODE(1,"GL Expedite – Air Mode"),//空运
    SURFACE_MODE(2,"GL Standard – Surface Mode"),//陆运
    SELF_PICK(3,"Self Pick up – Customer will come and pick up");//自取

    private DeliverGoodsTypeEnum(int type, String descr) {
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

