package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.common.utils.InvoiceResultCode;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.invoice.dao.CommonInvoiceMappingMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.CommonInvoiceMapping;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.stock.soa.common.utils.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by admin on 2017/3/16.
 */
@Service("commonInvoiceService")
public class CommonInvoiceServiceImpl implements CommonInvoiceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonInvoiceServiceImpl.class);

    @Autowired
    private CommonInvoiceMapper commonInvoiceMapper;

    @Autowired
    private CommonInvoiceMappingMapper commonInvoiceMappingMapper;

    @Override
    @Transactional
    public RemoteResult<CommonInvoice> addCommonInvoice(String lenovoId, String invoiceTitle, Integer shopid,String createBy) throws Exception {
        LOGGER.info("addCommonInvoice Start参数 : lenovoId="+lenovoId+";invoiceTitle="+invoiceTitle+";shopid="+shopid+";createBy="+createBy);

        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            Date date = new Date();
            CommonInvoice commonInvoice = new CommonInvoice();
            commonInvoice.setInvoiceTitle(invoiceTitle);
            commonInvoice.setShopid(shopid);
            commonInvoice = commonInvoiceMapper.getCommonInvoiceByTitle(commonInvoice);
            if (commonInvoice==null){
                //不存在相同的发票，可以添加
                commonInvoice.setInvoiceTitle(invoiceTitle);
                commonInvoice.setShopid(shopid);
                commonInvoice.setCreateBy(createBy);
                commonInvoice.setCreatetime(date);
                int i = commonInvoiceMapper.addCommonInvoice(commonInvoice);
                if (i==0){
                    remoteResult.setResultMsg("添加普通发票失败");
                    remoteResult.setResultCode(InvoiceResultCode.FAIL);
                    throw new Exception();
                }
            }

            CommonInvoiceMapping commonInvoiceMapping = new CommonInvoiceMapping();
            commonInvoiceMapping.setLenovoId(lenovoId);
            commonInvoiceMapping.setShopid(shopid);
            commonInvoiceMapping.setCommonInvoiceId(commonInvoice.getId());
            commonInvoiceMapping.setCreatetime(date);
            commonInvoiceMapping.setCreateBy(createBy);

            int j = commonInvoiceMappingMapper.addCommonInvoiceMapping(commonInvoiceMapping);
            if (j==0){
                remoteResult.setResultMsg("添加普通发票映射失败");
                remoteResult.setResultCode(InvoiceResultCode.FAIL);
                throw new Exception();
            }
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("添加成功");
            remoteResult.setSuccess(true);
            remoteResult.setT(commonInvoice);
        }catch (Exception e){
            remoteResult.setResultMsg("系统异常");
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
        LOGGER.info("addCommonInvoice End返回值 : "+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<CommonInvoice> getCommonInvoice(String lenovoId, Integer shopid){
        LOGGER.info("getCommonInvoice Start参数 : "+lenovoId+";"+shopid);

        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            CommonInvoiceMapping commonInvoiceMapping = new CommonInvoiceMapping();
            commonInvoiceMapping.setLenovoId(lenovoId);
            commonInvoiceMapping.setShopid(shopid);

            commonInvoiceMapping = commonInvoiceMappingMapper.getCommonInvoiceMapping(commonInvoiceMapping);

            if (commonInvoiceMapping==null){
                remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                remoteResult.setResultMsg("该用户没有任何普通发票记录");
                remoteResult.setSuccess(true);
                return remoteResult;
            }

            Integer commonInvoiceId = commonInvoiceMapping.getCommonInvoiceId();
            CommonInvoice commonInvoiceById = commonInvoiceMapper.getCommonInvoiceById(commonInvoiceId);

            remoteResult.setSuccess(true);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setT(commonInvoiceById);
        }catch (Exception e){
            remoteResult.setResultMsg("系统异常");
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getCommonInvoice End返回值 : "+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }
}
