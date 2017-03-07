package com.lenovo.invoice.service.message.param;

/**
 * Created by mayan3 on 2017/3/7.
 */
public class ThrowStatusParam {
    private int status;
    private String orderId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
