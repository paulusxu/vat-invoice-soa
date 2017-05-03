package com.lenovo.invoice.common.utils;

/**
 * Created by admin on 2017/3/16.
 */
public class InvoiceResultCode {

    public static final String FAIL = "99";//错误
    public static final String SUCCESS = "00";//成功
    public static final String ADDCOMMONINVOICEFAIL = "01";//添加新的普票失败
    public static final String ADDCOMMONINVOICEMAPPINGFAIL = "11";//添加普票映射失败
    public static final String UNEXCHANGEINVOICE = "02"; //订单已发货，不能进行换票操作
    public static final String ORDERSTATUSFAIL = "03"; //订单状态错误
    public static final String ADDVATINVOICEFAIL = "04";//添加新的增票失败
    public static final String UNEXCHANGEINVOICE_THROWORDER = "05";//订单不能抛送或者已抛送，不能换票
    public static final String ORDERTHROWSTATUSFAIL = "06";//订单抛送状态错误
    public static final String UPDATEORDERFAIL = "07";//修改订单失败
    public static final String THROWBTCPFAIL = "08";//抛BTCP失败
    public static final String PARAMSFAIL = "09";//必填参数错误
    public static final String GETORDERFAIL = "10";//获取订单信息失败
    public static final String GETORDERSTATUSFAIL = "12";//获取订单状态失败
    public static final String GETRECORDFAIL = "13";//查询不到换票记录




}
