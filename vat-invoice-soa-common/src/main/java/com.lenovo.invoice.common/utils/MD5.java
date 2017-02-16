package com.lenovo.invoice.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;


public class MD5 {

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