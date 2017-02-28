package com.lenovo.invoice.domain.param;

import java.io.Serializable;

/**
 * Created by mayan3 on 2016/6/22.
 */
public class GetVatInvoiceInfoListParam implements Serializable {
    private String lenovoId;
    private String faid;
    private String faType;

    public String getLenovoId() {
        return lenovoId;
    }

    public void setLenovoId(String lenovoId) {
        this.lenovoId = lenovoId;
    }

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }

    public String getFaType() {
        return faType;
    }

    public void setFaType(String faType) {
        this.faType = faType;
    }
}
