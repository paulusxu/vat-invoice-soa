package com.lenovo.invoice.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.lenovo.invoice.api.InvoiceShopApiService;
import com.lenovo.invoice.common.utils.ErrorUtils;
import com.lenovo.invoice.common.utils.InvoiceShopCode;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.common.utils.ShopCode;
import com.lenovo.invoice.dao.InvoiceShopMapper;
import com.lenovo.invoice.domain.InvoiceIdAndUuid;
import com.lenovo.invoice.domain.InvoiceShop;
import com.lenovo.invoice.domain.InvoiceShopModifyLog;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.tool.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xuweihua on 2016/7/26.
 */
@Service("invoiceShopApiService")
public class InvoiceShopApiServiceImpl implements InvoiceShopApiService {
    private static final Logger logger = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.17shop");
    @Autowired
    private InvoiceShopMapper invoiceShopMapper;

    @Transactional(rollbackFor = Exception.class)
    public RemoteResult<InvoiceIdAndUuid> synInvoice(InvoiceShop invoiceShop) throws Exception {
        logger.info("synInvoice参数>>" + JacksonUtil.toJson(invoiceShop));
        RemoteResult<InvoiceIdAndUuid> remoteResult = new RemoteResult<InvoiceIdAndUuid>(false);
        InvoiceIdAndUuid invoiceIdAndUuid = new InvoiceIdAndUuid();
        try {
            if (checkParameter(invoiceShop) > 0) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
                logger.info("synInvoice返回参数>>" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            InvoiceShopModifyLog invoiceShopModifyLog = new InvoiceShopModifyLog();
            ConvertUtils.register(new DateConverter(null), java.util.Date.class);
            ConvertUtils.register(new IntegerConverter(null), java.lang.Integer.class);
            invoiceShop.setCreateBy(invoiceShop.getLenovoID());
            BeanUtils.copyProperties(invoiceShopModifyLog, invoiceShop);
            if (invoiceShop.getIsDefault() == null) {
                invoiceShop.setIsDefault(0);
                invoiceShopModifyLog.setIsDefault(0);
            }
            if (invoiceShop.getSynType() == 1) {
                //判断customername,taxno 是否有单独存在的
                int validationInvoice=invoiceShopMapper.validationInvoice(invoiceShop.getLenovoID(),invoiceShop.getInvoiceType(),invoiceShop.getCustomerName(),invoiceShop.getTaxNo());
                if(validationInvoice>0){
                    remoteResult.setResultCode(ErrorUtils.ERR_CODE_CUSTOMERNAME_TAXNO_EXIST);
                    remoteResult.setResultMsg("公司名和税号不匹配，请核对后再试！");
                    logger.info("synInvoicePersonalCenter返回参数>>" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }

                String uuid = UUID.randomUUID().toString();
                invoiceShop.setUuid(uuid);
                invoiceShopMapper.addInvoiceShop(invoiceShop);
                invoiceIdAndUuid.setId(invoiceShop.getId());
                invoiceIdAndUuid.setUuid(uuid);
                remoteResult.setT(invoiceIdAndUuid);
                invoiceShopModifyLog.setUuid(uuid);
                invoiceShopModifyLog.setId(invoiceShop.getId());
                invoiceShopModifyLog.setCreateTime(new Date());
            } else if (invoiceShop.getSynType() == 2) {
                invoiceShop.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShop.setUpdateTime(new Date());
                invoiceShopModifyLog.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShopModifyLog.setUpdateTime(new Date());
                invoiceShopMapper.editInvoiceShop(invoiceShop);
            } else if (invoiceShop.getSynType() == 3) {
                RemoteResult<InvoiceShop> invoice = queryInvoiceForId(invoiceShop.getId() + "", invoiceShop.getLenovoID());
                BeanUtils.copyProperties(invoiceShopModifyLog, invoice.getT());
                invoiceShopModifyLog.setUuid(invoice.getT().getUuid());
                invoiceShopModifyLog.setSynType(3);
                invoiceShop.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShop.setUpdateTime(new Date());


                invoiceShopModifyLog.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShopModifyLog.setUpdateTime(new Date());
                invoiceShopMapper.delInvoiceShop(invoiceShop.getId() + "", invoiceShop.getLenovoID());
            }
            if (invoiceShop.getShopId() == ShopCode.smpsShop && !invoiceShop.isNoLog()) {
                invoiceShopMapper.addInvoiceShopLog(invoiceShopModifyLog);
            }
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
            remoteResult.setResultMsg("成功");

        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setSuccess(false);
            remoteResult.setResultMsg("失败");
            logger.info("synInvoice返回参数>>" + JacksonUtil.toJson(remoteResult));
            logger.error("synInvoice异常", e);
            throw e;
        }
        logger.info("synInvoice返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<List<InvoiceShop>> queryInvoice(String lenovoid) {
        logger.info("queryInvoice参数>>lenovoid=" + lenovoid);
        RemoteResult<List<InvoiceShop>> remoteResult = new RemoteResult<List<InvoiceShop>>();
        try {
            if (StringUtils.isEmpty(lenovoid)) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
            } else {
                List<InvoiceShop> invoiceShopList = invoiceShopMapper.queryInvoiceShop(lenovoid);
                remoteResult.setSuccess(true);
                remoteResult.setT(invoiceShopList);
                remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
                remoteResult.setResultMsg("成功！");
            }
        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setResultMsg("失败!");
            e.printStackTrace();
        }
        logger.info("queryInvoice返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<InvoiceShop> queryInvoiceForId(String id, String lenovoid) {
        logger.info("queryInvoiceForId参数>>id=" + id + ",lenovoid=" + lenovoid);
        RemoteResult<InvoiceShop> remoteResult = new RemoteResult<InvoiceShop>();
        try {
            if (StringUtils.isEmpty(id)) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
            } else {
                InvoiceShop invoice = invoiceShopMapper.queryInvoiceForId(id, lenovoid);
                remoteResult.setSuccess(true);
                remoteResult.setT(invoice);
                remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
                remoteResult.setResultMsg("成功！");
            }
        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setResultMsg("失败!");
            e.printStackTrace();
        }
        logger.info("queryInvoiceForId返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<List<InvoiceShopModifyLog>> queryInvoiceLog(String count) {
        logger.info("queryInvoiceLog参数>>count=" + count);
        RemoteResult<List<InvoiceShopModifyLog>> remoteResult = new RemoteResult<List<InvoiceShopModifyLog>>();
        try {
            if (StringUtils.isEmpty(count)) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
            } else {
                List<InvoiceShopModifyLog> invoices = invoiceShopMapper.queryInvoiceLog(count);
                remoteResult.setSuccess(true);
                remoteResult.setT(invoices);
                remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
                remoteResult.setResultMsg("成功！");
                StringBuilder ids = new StringBuilder();
                for (int i = 0; i < invoices.size(); i++) {
                    if (ids.length() > 0) {
                        ids.append("," + invoices.get(i).getLog_ID());
                    } else {
                        ids.append(invoices.get(i).getLog_ID());
                    }
                }
                if (invoices != null && invoices.size() > 0) {
                    invoiceShopMapper.updateLog(ids.toString());
                }
            }
        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setResultMsg("失败!");
            e.printStackTrace();
        }

        logger.info("queryInvoiceLog返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<Integer> getIdByUUID(String uuid) {
        logger.info("getIdByUUID参数>>uuid=" + uuid);
        RemoteResult<Integer> remoteResult = new RemoteResult<Integer>();
        try {
            if (StringUtils.isEmpty(uuid)) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
            } else {
                int id = invoiceShopMapper.getIdByUUID(uuid);
                remoteResult.setSuccess(true);
                remoteResult.setT(id);
                remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
                remoteResult.setResultMsg("成功！");
            }
        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setResultMsg("失败!");
            e.printStackTrace();
        }

        logger.info("getIdByUUID返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }


    @Transactional(rollbackFor = Exception.class)
    public RemoteResult modifyePersonalCenter(InvoiceShop invoiceShop) throws Exception {
        logger.info("synInvoicePersonalCenter参数>>" + JacksonUtil.toJson(invoiceShop));
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            if (checkParameter(invoiceShop) > 0) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
                logger.info("synInvoicePersonalCenter返回参数>>" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            InvoiceShopModifyLog invoiceShopModifyLog = new InvoiceShopModifyLog();
            ConvertUtils.register(new DateConverter(null), java.util.Date.class);
            ConvertUtils.register(new IntegerConverter(null), java.lang.Integer.class);
            invoiceShop.setCreateBy(invoiceShop.getLenovoID());
            BeanUtils.copyProperties(invoiceShopModifyLog, invoiceShop);
            if (invoiceShop.getIsDefault() == null) {
                invoiceShop.setIsDefault(0);
                invoiceShopModifyLog.setIsDefault(0);
            }
            if (invoiceShop.getSynType() == 1) {
                //判断customername,taxno 是否有单独存在的
                int validationInvoice=invoiceShopMapper.validationInvoice(invoiceShop.getLenovoID(),invoiceShop.getInvoiceType(),invoiceShop.getCustomerName(),invoiceShop.getTaxNo());
                if(validationInvoice>0){
                    remoteResult.setResultCode(ErrorUtils.ERR_CODE_CUSTOMERNAME_TAXNO_EXIST);
                    remoteResult.setResultMsg("公司名和税号不匹配，请核对后再试！");
                    logger.info("synInvoicePersonalCenter返回参数>>" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                String uuid = UUID.randomUUID().toString();
                invoiceShop.setUuid(uuid);
                invoiceShopMapper.addInvoiceShop(invoiceShop);
                invoiceShopModifyLog.setUuid(uuid);
                invoiceShopModifyLog.setId(invoiceShop.getId());
                invoiceShopModifyLog.setCreateTime(new Date());
            } else if (invoiceShop.getSynType() == 2) {
                invoiceShop.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShop.setUpdateTime(new Date());
                invoiceShopModifyLog.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShopModifyLog.setUpdateTime(new Date());
                if (invoiceShop.getIsDefault() == 1) {
                    invoiceShopMapper.editInvoiceIsDefault(invoiceShop.getLenovoID());
                }else {
                    invoiceShop.setApprovalStatus(3);
                }
                invoiceShopMapper.editInvoiceShop(invoiceShop);
            } else if (invoiceShop.getSynType() == 3) {
                RemoteResult<InvoiceShop> invoice = queryInvoiceForId(invoiceShop.getId() + "", invoiceShop.getLenovoID());
                BeanUtils.copyProperties(invoiceShopModifyLog, invoice.getT());
                invoiceShopModifyLog.setUuid(invoice.getT().getUuid());
                invoiceShopModifyLog.setSynType(3);

                invoiceShop.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShop.setUpdateTime(new Date());
                invoiceShopModifyLog.setUpdateBy(invoiceShop.getLenovoID());
                invoiceShopModifyLog.setUpdateTime(new Date());
                invoiceShopMapper.delInvoiceShop(invoiceShop.getId() + "", invoiceShop.getLenovoID());
            }
            invoiceShopMapper.addInvoiceShopLog(invoiceShopModifyLog);
            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
            remoteResult.setResultMsg("成功");

        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setSuccess(false);
            remoteResult.setResultMsg("失败");
            logger.info("synInvoicePersonalCenter返回参数>>" + JacksonUtil.toJson(remoteResult));
            e.printStackTrace();
            throw e;
        }
        logger.info("synInvoicePersonalCenter返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }


    @Override
    public RemoteResult<InvoiceShop> queryInvoiceAuditForId(String id) {
        logger.info("queryInvoiceAuditForId参数>>id=" + id);
        RemoteResult<InvoiceShop> remoteResult = new RemoteResult<InvoiceShop>();
        try {
            if (StringUtils.isEmpty(id)) {
                remoteResult.setResultCode(InvoiceShopCode.PARAMETER_ERROR_CODE);
                remoteResult.setResultMsg("参数错误！");
            } else {
                InvoiceShop invoice = invoiceShopMapper.queryInvoiceForId(id, null);
                if (invoice!=null&&invoice.getApprovalStatus()!=null&&invoice.getApprovalStatus() == 2) {
                    remoteResult.setSuccess(true);
                    remoteResult.setT(invoice);
                    remoteResult.setResultCode(InvoiceShopCode.SUCCESS);
                    remoteResult.setResultMsg("成功！");
                } else {
                    remoteResult.setResultCode(InvoiceShopCode.FAIL);
                    remoteResult.setResultMsg("未审核通过!");
                }
            }
        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceShopCode.FAIL);
            remoteResult.setResultMsg("失败!");
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        logger.info("queryInvoiceAuditForId返回参数>>" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }


    private int checkParameter(InvoiceShop invoiceShop) {
        if (invoiceShop == null) {
            return 9999;
        } else if (invoiceShop.getSynType() == 3) {
            return 0;
        } else if (invoiceShop.getInvoiceType() == null && invoiceShop.getSynType() != 2) {
            return 1;
        } else if (invoiceShop.getSynType() == null || invoiceShop.getSynType() > 3 || invoiceShop.getSynType() < 1) {
            return 2;
//        }else if(invoiceShop.getIsDefault()==null){
//            return 3;
//        }else if(invoiceShop.getApprovalStatus()==null){
//            return 4;
//        }else if(invoiceShop.getIsShow()==null){
//            return 5;
//        }else if(invoiceShop.getIsConfirmPersonal()==null){
//            return 6;
        } else if (invoiceShop.getShopId() == null) {
            return 7;
        } else if (invoiceShop.getSynType() == 2 || invoiceShop.getSynType() == 3) {
            if (invoiceShop.getId() == null) {
                return 8;
            }
        } else if (invoiceShop.getLenovoID() == null) {
            return 9;
        }else if (invoiceShop.getCompanyType() == null) {
            return 10;
        }
        return 0;
    }

    protected boolean isNull(Object... obj) {
        return !isNotNull(obj);
    }

    protected boolean isNotNull(Object... obj) {
        if (obj != null) {
            for (Object o : obj) {
                if (o == null) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

}
