package com.lenovo.invoice.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;


public class MD5 {

    public static void main(String[] args) {
        String str = sign("hello world","123","utf-8");
        System.out.println(str);
        System.out.println(str.length());
        System.out.println(str.substring(0, 16));
        System.out.println(str.substring(0,16).length());

        System.out.println(getMD5("hello world"));
    }

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {

        }
        return "";
    }

    public static String sign(String text, String key, String input_charset) {
        text = text + key;
        MessageDigest md5 = null;
        String value = "";
        try {
            md5 = MessageDigest.getInstance("MD5");
            sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
            value = baseEncoder.encode(md5.digest(text.getBytes("utf-8")));
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public static boolean verify(String text, String sign, String key, String input_charset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws java.security.SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出�?,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
}