package com.lenovo.invoice.common.utils;

/**
 * Created by admin on 2017/3/16.
 */
public class InvoiceResultCode {

    public static final String FAIL = "99";//错误
    public static final String SUCCESS = "00";//成功
    public static final String ADDCOMMONINVOICEFAIL = "01";//添加新的普票失败
    public static final String UNEXCHANGEINVOICE = "02"; //订单已发货，不能进行换票操作
    public static final String FAIDNOTALLOWEXCHANGE = "03"; //该订单faid不允许换票
    public static final String ADDVATINVOICEFAIL = "04";//添加新的增票失败
    public static final String NOINVOICEMAPPING = "05";//订单不能抛送或者已抛送，不能换票
    public static final String INVOICECHECKUPDATEFAIL = "06";//发票审核状态修改失败
    public static final String UPDATEORDERFAIL = "07";//修改订单失败
    public static final String THROWBTCPFAIL = "08";//抛BTCP失败
    public static final String PARAMSFAIL = "09";//必填参数错误
    public static final String GETORDERFAIL = "10";//获取订单信息失败
    public static final String ADDCOMMONINVOICEMAPPINGFAIL = "11";//添加普票映射失败
    public static final String GETORDERSTATUSFAIL = "12";//获取订单状态失败
    public static final String GETRECORDFAIL = "13";//查询不到换票记录
    public static final String ADDSPADDRESSFAIL = "14";//添加收票地址失败
    public static final String UPDATEVATINVOICEFAIL = "15";//修改增票信息失败
    public static final String GETPROVINCENOFAIL = "16";//获取省份编号失败
    public static final String NOTHISINVOICE = "17";//没有这张发票
    public static final String TAXNOFAIL = "18";//税号格式错误
    public static final String UPDATEINVOICEFAIL = "19"; //发票修改失败
    public static final String invoiceTitleIsExist = "20"; //存在已审核过的相同抬头！
    public static final String invoiceTaxNoIsExist = "21"; //存在已审核过的相同税号！
    public static final String ADD_DIAN_INVOICE_FAIL = "22";//添加新的电子票失败




}
