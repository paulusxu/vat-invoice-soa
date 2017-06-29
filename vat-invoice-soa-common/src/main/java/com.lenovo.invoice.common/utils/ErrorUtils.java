package com.lenovo.invoice.common.utils;

/**
 * Created by mayan3 on 2016/4/9.
 */
public class ErrorUtils {
    public static final String ERR_NOT_ALLOWED="2000";
    public static final String INVOICE_SUCCESS="1000";
    public static final String ERR_SYSTEM_EXCEPTION="10000";

    // 系统异常错误
    public static final String SYSTEM_UNKNOWN_EXCEPTION = "10001";
    // 必填的参数错误
    public static final String ERR_CODE_COM_REQURIE = "10002";
    //增值税发票信息不存在或未通过审核
    public static final String ERR_CODE_NOTEXIST_VAT = "10003";
     //增值税发票未共享
    public static final String ERR_CODE_NOT_SHARD = "10004";
    //增值税发票超过一条
    public static final String ERR_CODE_TOO_MORE_VAT = "10005";


    //增值税发票未通过BTCP审核，或者不是同一个商城
    public static final String ERR_CODE_SHARED = "10006";

    //已有相同的税号或抬头的发票，如需修改请联系客服！
    public static final String ERR_CODE_CUSTOMERNAME_TAXNO_EXIST = "10007";
    //增票信息不存在
    public static final String ERR_CODE_VATINVOICE_NOT_EXIST = "10008";

    //合同存在
    public static final String ERR_CODE_EXIST_CONTRACT = "20001";
}
