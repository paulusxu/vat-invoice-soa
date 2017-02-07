package com.lenovo.invoice.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证参数的正确与�?
 * Created by whw on 2015/9/9.
 */
public class CheckUtil {
    /**
     * 验证税号
     */
    public static boolean isTaxNo(String taxNo) {
        Pattern pattern = Pattern.compile("^.{15}$|^.{18}$|^.{20}$");
        Matcher matcher = pattern.matcher(taxNo);
        return matcher.matches();
    }

    /**
     * 手机号验�?
     * @autor
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        String p = "^[1][\\d]{10}$";
        return Pattern.compile(p).matcher(str).matches();
    }

    /**
     * 判断邮编
     * @param zipString 邮政编码
     * @return boolean
     */
    public static boolean isZipNO(String zipString){
        String str = "^[0-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }

    /**
     * 判断银行卡格式是否正�?
     * @param BackNo
     * @return
     */
    public static  boolean isBackNo(String BackNo){
        String str = "^[\\d]*";
        return Pattern.compile(str).matcher(BackNo).matches();
    }

    /**
     * 判断注册电话是否正确
     * @param phoneNo
     * @return
     */
    public static  boolean isPhone(String phoneNo){
        String str = "^(^[1][\\d]{10}$)|[0]{1}[0-9]{2,3}-[0-9]{7,8}$";
        return Pattern.compile(str).matcher(phoneNo).matches();
    }
}
