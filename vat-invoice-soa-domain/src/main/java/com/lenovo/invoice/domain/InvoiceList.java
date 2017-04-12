package com.lenovo.invoice.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuweihua on 2017/4/12.
 */
public class InvoiceList implements Serializable {
    private List<InvoiceType> pInvoiceTypes;
    private List<InvoiceType> zInvoiceTypes;

    public InvoiceList(List<InvoiceType> pInvoiceTypes,List<InvoiceType> zInvoiceTypes){
        this.pInvoiceTypes=pInvoiceTypes;
        this.zInvoiceTypes=zInvoiceTypes;
    }
    public List<InvoiceType> getpInvoiceTypes() {
        return pInvoiceTypes;
    }

    public void setpInvoiceTypes(List<InvoiceType> pInvoiceTypes) {
        this.pInvoiceTypes = pInvoiceTypes;
    }

    public List<InvoiceType> getzInvoiceTypes() {
        return zInvoiceTypes;
    }

    public void setzInvoiceTypes(List<InvoiceType> zInvoiceTypes) {
        this.zInvoiceTypes = zInvoiceTypes;
    }

}
