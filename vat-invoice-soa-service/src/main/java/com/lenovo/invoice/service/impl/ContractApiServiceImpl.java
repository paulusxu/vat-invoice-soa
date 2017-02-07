package com.lenovo.invoice.service.impl;

import com.google.common.base.Strings;
import com.lenovo.invoice.api.ContractApiService;
import com.lenovo.invoice.common.utils.ErrorUtils;
import com.lenovo.invoice.common.utils.FileUtils;
import com.lenovo.invoice.common.utils.HttpClientUtil;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.ContractMapper;
import com.lenovo.invoice.domain.Contract;
import com.lenovo.invoice.domain.param.AddContractInfoParam;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.invoice.service.ContractService;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.my.api.OrderApiService;
import com.lenovo.my.domain.ordermessage.ordermessageforsmb.Pact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by mayan3 on 2016/8/9.
 */
@Service("contractApiService")
public class ContractApiServiceImpl extends BaseService implements ContractApiService {
    private static final Logger logger = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.contract");

    @Autowired
    private ContractMapper contractMapper;
    @Autowired
    private ContractService contractService;

    @Autowired
    private OrderApiService orderApiService;

    @Override
    public RemoteResult<Contract> getContractInfo(String lenovoId, String cId) {
        logger.info("GetContractInfo Start: {},{}", lenovoId, cId);

        RemoteResult remoteResult = new RemoteResult(false);
        Contract contract = null;
        try {
            contract = contractService.getContractInfo(lenovoId, cId);
            if (contract == null) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_NOTEXIST_VAT);
                remoteResult.setResultMsg("暂时无合同信息下载");
            } else {
                remoteResult.setSuccess(true);
                remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
                remoteResult.setT(contract);
            }
        } catch (Exception e) {
            remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
            remoteResult.setResultMsg("系统异常");
            logger.error(e.getMessage(), e);
        }
        logger.info("GetContractInfo end:{}", JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public PageModel2<Contract> getContractPage(PageQuery pageQuery, Map map) {
        int count = contractMapper.getContractCount(map);
        pageQuery.setTotalCount(count);
        if (pageQuery.getTotalCount() == 0) {
            PageModel2<Contract> pageModel2 = new PageModel2<Contract>(pageQuery, new ArrayList<Contract>());
            return pageModel2;
        }
        int pageIndex = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int pageSize = pageQuery.getPageSize();
        map.put("pageIndex", pageIndex);//0
        map.put("pageSize", pageSize);//10
        List<Contract> contractList = contractMapper.getContractPage(map);
        return new PageModel2<Contract>(pageQuery, contractList);
    }

    @Override
    public RemoteResult addContractInfo(AddContractInfoParam param) {
        RemoteResult remoteResult = new RemoteResult(false);
        logger.info("AddContractInfo Start:{}", JacksonUtil.toJson(param));
        try {
            //判断入参是否为空
            if (isNull(param.getBuyerName(), param.getContractNo(), param.getIssend(), param.getLenovoId(),
                    param.getShipAddress(), param.getShipMobile(), param.getShipName(), param.getTakeffectTime())) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }
            Contract contract = contractService.getContractInfo(param.getLenovoId(), param.getContractNo());
            long rows = 0;
            if (contract != null) {
                contract.setIssend(param.getIssend() ? 1 : 0);
                contract.setShipName(param.getShipName());
                contract.setShipMobile(param.getShipMobile());
                contract.setShipAddress(param.getShipAddress());
                contract.setBuyerName(param.getBuyerName());

                rows = contractService.updateContractInfo(contract);
                if (rows > 0) {
                    //生成pdf
                    Properties properties = loadProperty();
                    String url = properties.getProperty("smb.url") + "/contract/downloadContractFoId.jhtm" + "?orderCode=" + contract.getOrderCode() + "&lenovoId=" + contract.getLenovoId() + "&cId=" + contract.getContractNo();
                    HttpClientUtil.getStr(url);
                }
            } else {
                rows = contractService.addContractInfo(param);
            }
            contract = contractService.getContractInfo(param.getLenovoId(), param.getContractNo());
            if (rows > 0) {
                remoteResult.setSuccess(true);
                remoteResult.setT(contract);
                remoteResult.setResultMsg("保存成功");
                remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("AddContractInfo End:{}", JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<Pact> getPactInfo(String orderCode,String userId,int shopId) {
        RemoteResult remoteResult = new RemoteResult(false);

        Pact pact=orderApiService.getOrderMessageByOrderMainCode(orderCode, userId, String.valueOf(shopId));
        if(pact!=null)
        {
            remoteResult.setSuccess(true);
            remoteResult.setT(pact);
        }
        return remoteResult;
    }

    @Override
    public RemoteResult uploadFile(byte[] buf, String lenovoId, String cId) {
        RemoteResult remoteResult = new RemoteResult(false);
        String url = FileUtils.accessUrl;
        try {

            remoteResult = getContractInfo(lenovoId, cId);
            if (remoteResult.isSuccess()) {
                String filePath = FileUtils.fileUpload(new ByteArrayInputStream(buf));
                if (!Strings.isNullOrEmpty(filePath)) {
                    url = url + filePath;
                }
                Contract contract = (Contract) remoteResult.getT();
                contract.setUrl(url);
                contractService.updateContractInfo(contract);
            }
            logger.info("uploadFile:{},{},{}", lenovoId, cId, url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return remoteResult;
    }
}
