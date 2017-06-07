package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.common.utils.InvoiceResultCode;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.invoice.dao.CommonInvoiceMappingMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public RemoteResult<CommonInvoice> addCommonInvoice(CommonInvoice commonInvoice) throws Exception {
        LOGGER.info("addCommonInvoice参数=="+JacksonUtil.toJson(commonInvoice));

        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            String lenovoId = commonInvoice.getLenovoId();
            String invoiceTitle = commonInvoice.getInvoiceTitle();
            Integer shopid = commonInvoice.getShopid();
            String createBy = commonInvoice.getCreateBy();
            Integer type = commonInvoice.getType();
            String taxNo = commonInvoice.getTaxNo();

            if (isNull(lenovoId,invoiceTitle,shopid,createBy,type) || (type==1 && isNull(taxNo))){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("addCommonInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            LOGGER.info("commonInvoiceMapper" + commonInvoiceMapper);
            CommonInvoice commonInvoice2 = commonInvoiceMapper.getCommonInvoiceByTitle(commonInvoice);
            if (commonInvoice2==null){
                //不存在相同的发票，可以添加
                int i = commonInvoiceMapper.addCommonInvoice(commonInvoice);
                if (i==0){
                    remoteResult.setResultMsg("添加普通发票失败");
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    LOGGER.info("addCommonInvoice返回值=="+ JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
            }else {
                commonInvoice.setId(commonInvoice2.getId());
            }

            int j = commonInvoiceMappingMapper.addCommonInvoiceMapping(commonInvoice);
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
    public RemoteResult<CommonInvoice> getCommonInvoice(CommonInvoice commonInvoice){
        LOGGER.info("getCommonInvoice参数=="+JacksonUtil.toJson(commonInvoice));
        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            String lenovoId = commonInvoice.getLenovoId();
            Integer shopid = commonInvoice.getShopid();
            Integer type = commonInvoice.getType();
            if (isNull(lenovoId,shopid,type)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("getCommonInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            CommonInvoice commonInvoice1 = commonInvoiceMappingMapper.getCommonInvoiceMapping(commonInvoice);

            if (commonInvoice1==null){
                remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                remoteResult.setResultMsg("该用户没有任何普通发票记录");
                remoteResult.setSuccess(true);
                return remoteResult;
            }

            Integer id = commonInvoice1.getId();
            commonInvoice = commonInvoiceMapper.getCommonInvoiceById(id);

            remoteResult.setSuccess(true);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setT(commonInvoice);
        }catch (Exception e){
            remoteResult.setResultMsg("系统异常");
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getCommonInvoice返回值=="+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }
}
