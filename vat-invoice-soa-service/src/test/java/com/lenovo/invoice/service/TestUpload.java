package com.lenovo.invoice.service;

import com.lenovo.fileclient.UploadManager;
import com.lenovo.fileclient.common.UploadResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

/**
 * Created by mayan3 on 2016/8/9.
 */
public class TestUpload {

    public static void main(String[] args) throws FileNotFoundException {
        //用户域
        String appId = "goods";
        //密钥
        String appKey = "gjj4sXiMC4dj9Zwp";
        //自定义文件路径+名称
        String newFileName = "/contract/"+ UUID.randomUUID().toString().replace("-","")+".jpg";
        //服务器的url
        String url = "http://up.lefile.cn/image/upload";
        //要上传的本地文件路径
        String srcfilePath = "d:/11.jpg";
        //是否强制覆盖服务器上已有文件
        Boolean update = false;
        //上传并打印响应码
//        UploadResponse uploadResponse = UploadManager.uploadFile(appId, appKey, url,newFileName, srcfilePath, update);
        UploadResponse uploadResponse = UploadManager.upload(appId, appKey, url, newFileName, new FileInputStream(new File(srcfilePath)), "xx", update);
        System.out.println(uploadResponse.getStatus());
    }
}
