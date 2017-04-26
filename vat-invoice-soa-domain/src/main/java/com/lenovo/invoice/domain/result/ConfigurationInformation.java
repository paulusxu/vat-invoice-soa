package com.lenovo.invoice.domain.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuweihua on 2017/4/26.
 */
public class ConfigurationInformation implements Serializable {
    private List<FaInvoiceResult> faInvoiceResults;
    private List<PaymentType> paymentTypes;

    public List<FaInvoiceResult> getFaInvoiceResults() {
        return faInvoiceResults;
    }

    public void setFaInvoiceResults(List<FaInvoiceResult> faInvoiceResults) {
        this.faInvoiceResults = faInvoiceResults;
    }

    public List<PaymentType> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<PaymentType> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
