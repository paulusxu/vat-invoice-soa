package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.common.utils.InvoiceResultCode;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.CommonInvoiceMapper;
import com.lenovo.invoice.dao.CommonInvoiceMappingMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.m2.arch.framework.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/3/16.
 */
@Service("commonInvoiceService")
public class CommonInvoiceServiceImpl extends BaseService implements CommonInvoiceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonInvoiceServiceImpl.class);
    private static final Logger errorLogger = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.commonInvoice");

    @Autowired
    private CommonInvoiceMapper commonInvoiceMapper;

    @Autowired
    private CommonInvoiceMappingMapper commonInvoiceMappingMapper;

    @Override
    @Transactional
    //添加普通发票
    public RemoteResult<CommonInvoice> addCommonInvoice(CommonInvoice commonInvoice,Tenant tenant) throws Exception {
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
            List<CommonInvoice> commonInvoiceMappingByIds = commonInvoiceMappingMapper.getCommonInvoiceMappingByIds(commonInvoice);
            if (commonInvoiceMappingByIds==null || commonInvoiceMappingByIds.size()==0){
                int j = commonInvoiceMappingMapper.addCommonInvoiceMapping(commonInvoice);
                if (j==0){
                    remoteResult.setResultMsg("添加普通发票映射失败");
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEMAPPINGFAIL);
                    throw new Exception();
                }
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
    public RemoteResult<CommonInvoice> getCommonInvoice(CommonInvoice commonInvoice,Tenant tenant){
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
                remoteResult.setResultCode(InvoiceResultCode.NOINVOICEMAPPING);
                remoteResult.setResultMsg("该用户没有任何普通发票记录");
                LOGGER.info("getCommonInvoice返回值==" + JacksonUtil.toJson(remoteResult));
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

    @Override
    public RemoteResult<CommonInvoice> getCommonInvoiceByIds(String lenovoId, Integer id,Tenant tenant) {
        LOGGER.info("getCommonInvoiceByIds参数=="+lenovoId+"=="+id);
        RemoteResult<CommonInvoice> remoteResult = new RemoteResult<CommonInvoice>();

        try {
            if (isNull(lenovoId,id)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("getCommonInvoiceByIds返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            CommonInvoice commonInvoice = new CommonInvoice();
            commonInvoice.setLenovoId(lenovoId);
            commonInvoice.setId(id);
            List<CommonInvoice> commonInvoiceMappingByIds = commonInvoiceMappingMapper.getCommonInvoiceMappingByIds(commonInvoice);
            if (commonInvoiceMappingByIds==null || commonInvoiceMappingByIds.size()==0){
                remoteResult.setResultMsg("该用户没有这张发票！");
                remoteResult.setResultCode(InvoiceResultCode.NOTHISINVOICE);
                LOGGER.info("getCommonInvoiceByIds返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            commonInvoice = commonInvoiceMapper.getCommonInvoiceById(id);
            commonInvoice.setLenovoId(lenovoId);

            remoteResult.setSuccess(true);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setT(commonInvoice);
        }catch (Exception e){
            remoteResult.setResultMsg("系统异常");
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getCommonInvoiceByIds返回值==" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //以下为添加税号后方法。这里只操作普票和电子票

    //分页查询，后台审核使用
    public RemoteResult<PageModel2<VatInvoice>> getInvoiceByPage(PageQuery pageQuery,VatInvoice vatInvoice){
        LOGGER.info("getInvoiceByPage==参数=="+JacksonUtil.toJson(pageQuery)+"=="+JacksonUtil.toJson(vatInvoice));
        RemoteResult<PageModel2<VatInvoice>> remoteResult = new RemoteResult<PageModel2<VatInvoice>>();
        try {
            PageModel<VatInvoice> invoiceByPage = commonInvoiceMapper.getInvoiceByPage(pageQuery, vatInvoice);
            PageModel2<VatInvoice> pageModel2 = new PageModel2<VatInvoice>(invoiceByPage);
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setT(pageModel2);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getInvoiceByPage==返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //后台审核通过接口
    @Override
    public RemoteResult checkInvoice(VatInvoice vatInvoice) {
        LOGGER.info("checkInvoice==参数=="+JacksonUtil.toJson(vatInvoice));
        RemoteResult remoteResult = new RemoteResult();
        try {
            Long id = vatInvoice.getId();
            String checkBy = vatInvoice.getCheckBy();
            if (isNull(id,checkBy)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数为空！");
                LOGGER.info("checkInvoice==返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            //TODO 判断是否存在已审核过的相同发票
            //存在，删除这个，刷新订单 TODO
            //不存在，修改发票状态
            int i = commonInvoiceMapper.updateInvoiceIsCheck(vatInvoice);
            if (i==0){
                remoteResult.setResultCode(InvoiceResultCode.INVOICECHECKUPDATEFAIL);
                remoteResult.setResultMsg("审核状态修改失败！");
                LOGGER.info("checkInvoice==返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            //修改成功，通知订单 TODO

            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("审核通过！");
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("checkInvoice==返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //后台修改发票信息接口
    @Override
    public RemoteResult updateInvoice(VatInvoice vatInvoice) {
        LOGGER.info("updateInvoice==参数=="+JacksonUtil.toJson(vatInvoice));
        RemoteResult remoteResult = new RemoteResult();
        try {
            //判断是否已存在审核过的该发票 TODO
            //存在，刷订单 TODO
            //不存在，修改发票
            int i = commonInvoiceMapper.updateInvoice(vatInvoice);
            if (i==0){
                remoteResult.setResultCode(InvoiceResultCode.UPDATEINVOICEFAIL);
                remoteResult.setResultMsg("发票修改失败！");
                LOGGER.info("updateInvoice==返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("修改成功！");
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("updateInvoice==返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //查询单个发票信息
    public RemoteResult<VatInvoice> getInvoiceById(Long id,Tenant tenant){
        LOGGER.info("getInvoiceById==参数=="+id);
        RemoteResult<VatInvoice> remoteResult = new RemoteResult<VatInvoice>();
        try {
            VatInvoice invoiceById = commonInvoiceMapper.getInvoiceById(id);
            if (invoiceById==null){
                remoteResult.setResultCode(InvoiceResultCode.NOTHISINVOICE);
                remoteResult.setResultMsg("没有这张发票！");
                LOGGER.info("getInvoiceById==返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setT(invoiceById);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getInvoiceById==返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //前台页面，保存发票信息
    @Override
    public RemoteResult<VatInvoice> saveInvoice(VatInvoice vatInvoice, Tenant tenant) {
        LOGGER.info("saveInvoice==参数=="+JacksonUtil.toJson(vatInvoice));
        RemoteResult<VatInvoice> remoteResult = new RemoteResult<VatInvoice>();
        try {
            String customername = vatInvoice.getCustomername();
            String taxno = vatInvoice.getTaxno();
            String createby = vatInvoice.getCreateby();
            int shopid = vatInvoice.getShopid();
            Integer taxNoType = vatInvoice.getTaxNoType();
            Integer invoiceType = vatInvoice.getInvoiceType();
            Integer custType = vatInvoice.getCustType();
            if (isNull(customername,createby,shopid,custType,invoiceType)||(custType==1&&isNull(taxNoType))||(custType==1&&taxNoType!=3&&isNull(taxno))){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数为空！");
                LOGGER.info("saveInvoice==返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            if (taxNoType!=3){
                //验证位数，转大写
                Pattern pattern;
                if (taxNoType==1){
                    pattern = Pattern.compile("^.{15}$");
                }else{
                    pattern = Pattern.compile("^.{18}$");
                }
                Matcher matcher = pattern.matcher(taxno);
                if (matcher.matches()) {
                    taxno = taxno.toUpperCase();
                } else {
                    remoteResult.setResultCode(InvoiceResultCode.TAXNOFAIL);
                    remoteResult.setResultMsg("税号格式错误！");
                    LOGGER.info("saveInvoice==返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
            }
            //判断该发票是否已经存在
            VatInvoice invoiceIsExist = commonInvoiceMapper.invoiceIsExist(vatInvoice);
            if (invoiceIsExist==null){
                vatInvoice.setTaxno(taxno);
                if (custType==0||(custType==1&&taxNoType==3)){
                    vatInvoice.setIscheck(1);
                    vatInvoice.setIsvalid(1);
                }else {
                    vatInvoice.setIscheck(0);
                    vatInvoice.setIsvalid(0);
                }
                int i = commonInvoiceMapper.saveInvoice(vatInvoice);
                if (i==0){
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新发票失败！");
                    LOGGER.info("saveInvoice==返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }else {
                    remoteResult.setT(vatInvoice);
                }
            }else {
                remoteResult.setT(invoiceIsExist);
            }
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("保存成功！");
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("saveInvoice==返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //前台页面根据发票抬头带出发票信息，必须是已审核的
    @Override
    public RemoteResult<VatInvoice> getInvoiceByTitle(VatInvoice vatInvoice, Tenant tenant) {
        LOGGER.info("getInvoiceByTitle==参数=="+JacksonUtil.toJson(vatInvoice));
        RemoteResult<VatInvoice> remoteResult = new RemoteResult<VatInvoice>();
        try {
            VatInvoice invoiceByTitle = commonInvoiceMapper.getInvoiceByTitle(vatInvoice);
            if (invoiceByTitle==null){
                remoteResult.setResultCode(InvoiceResultCode.NOTHISINVOICE);
                remoteResult.setResultMsg("未查询到发票信息！");
                LOGGER.info("getInvoiceByTitle==返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("查询成功！");
            remoteResult.setT(invoiceByTitle);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getInvoiceByTitle==返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }


}
