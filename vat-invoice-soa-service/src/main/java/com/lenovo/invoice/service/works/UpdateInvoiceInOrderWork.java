package com.lenovo.invoice.service.works;

import com.lenovo.invoice.common.utils.PropertiesUtil;
import com.lenovo.invoice.dao.ExchangeInvoiceRecordMapper;
import com.lenovo.invoice.domain.UpdateInvoiceInOrderParams;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by admin on 2017/3/21.
 */
public class UpdateInvoiceInOrderWork {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateInvoiceInOrderWork.class);

    @Autowired
    private PropertiesUtil propertiesUtil;

    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;

    @Autowired
    private ExchangeInvoiceRecordMapper exchangeInvoiceRecordMapper;

    public void execute(){
        LOGGER.info("执行修改订单重复发送请求work！");
        try {
            List<UpdateInvoiceInOrderParams> paramsList = exchangeInvoiceRecordMapper.getErrorUpdateOrder();

            for (UpdateInvoiceInOrderParams params : paramsList) {
                Invoice invoice = new Invoice();
                invoice.setOrderId(params.getOrderCode());
                invoice.setType(params.getInvoiceType());
                invoice.setTitle(params.getInvoiceTitle());
                invoice.setTaxpayerIdentity(params.getTaxNo());
                invoice.setDepositBank(params.getBankName());
                invoice.setBankNo(params.getBankNo());
                invoice.setRegisterAddress(params.getAddress());
                invoice.setRegisterPhone(params.getPhone());
                RemoteResult remoteResult = vatApiOrderCenter.updateInvoice(invoice);
                if (remoteResult.isSuccess()){
                    try {
                        //如果修改成功，删除这条数据
                        int i = exchangeInvoiceRecordMapper.deleteErrorUpdateOrder(params.getId());
                        if (i==0){
                            //删除失败，打印日志
                            LOGGER.info("订单修改成功，但是错误记录删除失败！"+params.getId());
                        }
                    }catch (Exception e){
                        LOGGER.error("订单修改成功，但是错误记录删除失败！"+params.getId()+e.getMessage(),e);
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

}
