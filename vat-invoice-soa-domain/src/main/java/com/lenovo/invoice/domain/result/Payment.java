package com.lenovo.invoice.domain.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuweihua on 2017/5/10.
 */
public class Payment implements Serializable {
    private PaymentType defaultType;
    private List<PaymentType> paymentTypes;

    public PaymentType getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(PaymentType defaultType) {
        this.defaultType = defaultType;
    }

    public List<PaymentType> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<PaymentType> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
