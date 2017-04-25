package com.lenovo.invoice.domain.result;

import com.lenovo.invoice.domain.InvoiceList;

import java.io.Serializable;

/**
 * Created by xuweihua on 2017/4/12.
 */
public class FaInvoiceResult implements Serializable {
    private String faid;
    private InvoiceList invoiceList;

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }

    public InvoiceList getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(InvoiceList invoiceList) {
        this.invoiceList = invoiceList;
    }
}
