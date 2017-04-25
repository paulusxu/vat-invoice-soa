package com.lenovo.invoice.domain.param;

import java.io.Serializable;

/**
 * Created by xuweihua on 2017/4/12.
 */
public class FaData  implements Serializable {
    private int fatype;
    private String faid;

    public FaData(String faid,int fatype){
        this.faid=faid;
        this.fatype=fatype;
    }

    public int getFatype() {
        return fatype;
    }

    public void setFatype(int fatype) {
        this.fatype = fatype;
    }

    public String getFaid() {
        return faid;
    }

    public void setFaid(String faid) {
        this.faid = faid;
    }
}
