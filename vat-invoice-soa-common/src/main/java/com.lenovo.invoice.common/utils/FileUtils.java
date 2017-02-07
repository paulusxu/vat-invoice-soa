package com.lenovo.invoice.common.utils;

import com.lenovo.fileclient.UploadManager;
import com.lenovo.fileclient.common.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by mayan3 on 2016/8/11.
 */
public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.contract");

    private static final String appId = "goods";
    private static final String appKey = "gjj4sXiMC4dj9Zwp";
    private static final String url = "http://up.lefile.cn/image/upload";
    public static final String accessUrl="http://goods.app.lefile.cn/";

    public static String fileUpload(InputStream inputStream) {
        //自定义文件路径+名称
        String newFileName = "/contract/" + UUID.randomUUID().toString().replace("-", "") + ".pdf";

        //上传并打印响应码
        UploadResponse uploadResponse = UploadManager.upload(appId, appKey, url, newFileName, inputStream, "xx", false);
        logger.info("uploadResponse:",JacksonUtil.toJson(uploadResponse));
        if (uploadResponse != null && uploadResponse.getStatus().equals("001")) {
            return uploadResponse.getFilename();
        }
    return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream is=new FileInputStream(new File("d:/11.pdf"));
        System.out.println(fileUpload(null));
        System.out.println(JacksonUtil.toJson(new UploadResponse()));
        logger.info("uploadResponse:", JacksonUtil.toJson(null));
    }
}
