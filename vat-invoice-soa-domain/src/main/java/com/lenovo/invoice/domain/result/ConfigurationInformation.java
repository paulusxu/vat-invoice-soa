package com.lenovo.invoice.domain.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuweihua on 2017/4/26.
 */
public class ConfigurationInformation implements Serializable {
    private List<FaInvoiceResult> faInvoiceResults;
    private Payment payment;
    private DeliverGoods deliverGoods;

    public DeliverGoods getDeliverGoods() {
        return deliverGoods;
    }

    public void setDeliverGoods(DeliverGoods deliverGoods) {
        this.deliverGoods = deliverGoods;
    }

    public List<FaInvoiceResult> getFaInvoiceResults() {
        return faInvoiceResults;
    }

    public void setFaInvoiceResults(List<FaInvoiceResult> faInvoiceResults) {
        this.faInvoiceResults = faInvoiceResults;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
