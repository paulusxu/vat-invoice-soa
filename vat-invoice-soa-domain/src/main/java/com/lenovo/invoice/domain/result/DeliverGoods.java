package com.lenovo.invoice.domain.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuweihua on 2017/7/26.
 */
public class DeliverGoods  implements Serializable {
    private DeliverGoodsTypeEnum defaultType;
    private List<DeliverGoodsTypeEnum> deliverGoodsList;

    public DeliverGoodsTypeEnum getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(DeliverGoodsTypeEnum defaultType) {
        this.defaultType = defaultType;
    }

    public List<DeliverGoodsTypeEnum> getDeliverGoodsList() {
        return deliverGoodsList;
    }

    public void setDeliverGoodsList(List<DeliverGoodsTypeEnum> deliverGoodsList) {
        this.deliverGoodsList = deliverGoodsList;
    }
}
