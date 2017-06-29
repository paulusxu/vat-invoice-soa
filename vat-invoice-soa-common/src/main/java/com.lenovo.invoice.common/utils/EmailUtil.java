package com.lenovo.invoice.common.utils;

import com.lenovo.email.client.EmailServicesClient;
import com.lenovo.email.model.MulitySimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EmailUtil {

    @Autowired
    private PropertiesUtil propertiesUtil;

    public void sendEmail(String title,String content){
        MulitySimpleEmail mulitySimpleEmail = new MulitySimpleEmail();
        long appId = 0;
        boolean showAllAddress = false;//是否同时显示所有的收件人
        boolean useMarket = false;//是否是营销邮件
        mulitySimpleEmail.setAppId(appId);
        mulitySimpleEmail.setSendAt(new Date());//邮件发送日期
        mulitySimpleEmail.setTitle(title);//邮件标题
        mulitySimpleEmail.setContent(content);//邮件正文
        List<String> toList = new ArrayList<>();
        String [] emails=propertiesUtil.getInvoiceEmails().split(",");
        Collections.addAll(toList, emails);
        mulitySimpleEmail.setToAddress(toList);//收件人列表
        EmailServicesClient.getInstance().getEmailService().sendEmail(mulitySimpleEmail,showAllAddress,useMarket);

    }
}
