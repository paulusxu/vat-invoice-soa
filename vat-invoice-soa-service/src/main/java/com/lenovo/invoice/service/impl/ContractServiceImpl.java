package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.common.utils.HttpClientUtil;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.ContractMapper;
import com.lenovo.invoice.domain.Contract;
import com.lenovo.invoice.domain.param.AddContractInfoParam;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.invoice.service.ContractService;

import com.lenovo.my.api.OrderApiService;
import com.lenovo.my.domain.ordermessage.ordermessageforsmb.Pact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by mayan3 on 2016/8/9.
 */
@Service("contractService")
public class ContractServiceImpl extends BaseService implements ContractService {
    private static final Logger logger = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.contract");

    @Autowired
    private ContractMapper contractMapper;
    @Autowired
    private OrderApiService orderApiService;

    @Override
    public void getContractInfo(String orderCode,String userId,int shopId) {
        try {
            //调用订单接口
            Pact pact = orderApiService.getOrderMessageByOrderMainCode(orderCode, userId, String.valueOf(shopId));
            logger.info("GetContractInfo Start：" + JacksonUtil.toJson(pact));
            if (pact!=null) {
                    //初始化合同信息
                    AddContractInfoParam param = new AddContractInfoParam();
                    param.setBuyerName(pact.getBuyerName());
                    param.setContractNo(pact.getCode());
                    param.setIssend(pact.isNeedToSend());
                    param.setShipAddress(pact.getShipAddress());
                    param.setShipMobile(pact.getShipMobile());
                    param.setShipName(pact.getShipName());
                    param.setLenovoId(pact.getLenovoId());
                    param.setTakeffectTime(pact.getTakeEffectTime());
                    param.setOrderCode(orderCode);

                    long rows = addContractInfo(param);
                    if (rows > 0) {
                        //生成pdf
                        Properties properties = loadProperty();
                        String url = properties.getProperty("smb.url")+"/contract/downloadContractFoId.jhtm" + "?orderCode=" + orderCode + "&lenovoId=" + pact.getLenovoId() + "&cId=" + pact.getCode();
                        HttpClientUtil.getStr(url);
                    }
            }
        } catch (Exception e) {
            logger.error("GetContractInfo error!", e);
        }
    }

    @Override
    public long addContractInfo(AddContractInfoParam param) {
        long rows = 0;
        try {
            rows = contractMapper.insertContractInfo(processContract(param));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rows;
    }

    public Contract processContract(AddContractInfoParam param) {
        Contract contract = new Contract();
        contract.setBuyerName(param.getBuyerName());
        contract.setContractNo(param.getContractNo());
        contract.setIssend(param.getIssend() ? 1 : 0);
        contract.setShipAddress(param.getShipAddress());
        contract.setShipMobile(param.getShipMobile());
        contract.setShipName(param.getShipName());
        contract.setLenovoId(param.getLenovoId());
        contract.setOrderCode(param.getOrderCode());
        contract.setTakeffectTime(param.getTakeffectTime());
        return contract;
    }

    @Override
    public Contract getContractInfo(String lenovoId, String cId) {
        Contract contract = null;
        try {
            contract = contractMapper.getContractInfo(lenovoId, cId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return contract;
    }

    @Override
    public long updateContractInfo(Contract contract) {
        long rows = 0;
        try {
            rows = contractMapper.updateContractInfo(contract);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rows;
    }


}
