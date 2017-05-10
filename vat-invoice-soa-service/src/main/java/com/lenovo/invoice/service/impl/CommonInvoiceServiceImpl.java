package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.common.utils.InvoiceResultCode;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.invoice.dao.CommonInvoiceMappingMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.CommonInvoiceMapping;
import com.lenovo.invoice.service.BaseService;
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
public class CommonInvoiceServiceImpl extends BaseService implements CommonInvoiceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonInvoiceServiceImpl.class);

    @Autowired
    private CommonInvoiceMapper commonInvoiceMapper;

    @Autowired
    private CommonInvoiceMappingMapper commonInvoiceMappingMapper;

    @Override
    @Transactional
    //添加普通发票
    public RemoteResult<CommonInvoice> addCommonInvoice(String lenovoId, String invoiceTitle, Integer shopid,String createBy,Integer type) throws Exception {
        LOGGER.info("addCommonInvoice参数==lenovoId="+lenovoId+";invoiceTitle="+invoiceTitle+";shopid="+shopid+";createBy="+createBy+";type="+type);

        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            if (isNull(lenovoId,invoiceTitle,shopid,createBy,type)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("addCommonInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            Date date = new Date();
            CommonInvoice commonInvoice = new CommonInvoice();
            commonInvoice.setInvoiceTitle(invoiceTitle);
            commonInvoice.setShopid(shopid);
            commonInvoice.setType(type);
            commonInvoice = commonInvoiceMapper.getCommonInvoiceByTitle(commonInvoice);
            if (commonInvoice==null){
                //不存在相同的发票，可以添加
                commonInvoice.setInvoiceTitle(invoiceTitle);
                commonInvoice.setShopid(shopid);
                commonInvoice.setType(type);
                commonInvoice.setCreateBy(createBy);
                commonInvoice.setCreatetime(date);
                LOGGER.info("commonInvoiceMapper"+commonInvoiceMapper);
                int i = commonInvoiceMapper.addCommonInvoice(commonInvoice);
                if (i==0){
                    remoteResult.setResultMsg("添加普通发票失败");
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
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
                remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEMAPPINGFAIL);
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
        LOGGER.info("addCommonInvoice返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    //获取用户最近一次开的普通发票，区分公司和个人
    public RemoteResult<CommonInvoice> getCommonInvoice(String lenovoId, Integer shopid,Integer type){
        LOGGER.info("getCommonInvoice参数==lenovoId="+lenovoId+";shopid="+shopid+";type="+type);

        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            if (isNull(lenovoId,shopid,type)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("getCommonInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            CommonInvoiceMapping commonInvoiceMapping = new CommonInvoiceMapping();
            commonInvoiceMapping.setLenovoId(lenovoId);
            commonInvoiceMapping.setShopid(shopid);
            commonInvoiceMapping.setType(type);

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
        LOGGER.info("getCommonInvoice返回值=="+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }
}
