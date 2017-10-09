package com.lenovo.invoice.common.utils;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.security.MessageDigest;
import java.sql.Timestamp;

/**
 * Created by epcm on 2017/8/29.
 */
public class AuthKeyGen {

    private static final Logger LOG = Logger.getLogger(AuthKeyGen.class);

    public static String getAuthKey(String client_id, String client_secret) {

        Timestamp now = new Timestamp(System.currentTimeMillis());

        String authInfo_plain = client_id + client_secret + "AT" + now.getTime();

        String authInfo_md5 = getMD5(authInfo_plain, 16);
//        String authInfo_md5 = SimpleMD5.getMD5(authInfo_plain, 16);

        LOG.info(authInfo_md5);

        return authInfo_md5 + "." + now.getTime() ;


    }


    private static String getMD5(String text, int length) {

        StringBuilder sb = new StringBuilder(40);

        try {

            MessageDigest MD5 = MessageDigest.getInstance("MD5");

            byte[] info_mid =  MD5.digest(text.getBytes());

            for(byte x : info_mid){

                if((x & 0xff)>>4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if(length == 16)
            return sb.toString().substring(8, 24);
        else
            return sb.toString();

    }
}
