package com.lenovo.invoice.domain.result;

/**
 * Created by xuweihua on 2017/7/26.
 */
public enum DeliverGoodsTypeEnum {
    AIRE_MODE("L1","Air"),//空运
    SURFACE_MODE("L2","Surface"),//陆运
    SELF_PICK("S2","Self Pickup");//自取

    private DeliverGoodsTypeEnum(String type, String descr) {
        this.type = type;
        this.descr = descr;
    }
    private final String type;
    private final String descr;

    public String getType() {
        return type;
    }

    public String getDescr() {
        return descr;
    }
}

