package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.ExchangeInvoiceService;
import com.lenovo.invoice.common.utils.*;
import com.lenovo.invoice.dao.ExchangeInvoiceRecordMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.invoice.domain.UpdateInvoiceInOrderParams;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
import com.lenovo.invoice.domain.result.AddVatInvoiceInfoResult;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.stock.soa.common.utils.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by admin on 2017/3/19.
 */
@Service("exchangeInvoiceService")
public class ExchangeInvoiceServiceImpl implements ExchangeInvoiceService{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeInvoiceServiceImpl.class);

    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;

    @Autowired
    private ExchangeInvoiceRecordMapper exchangeInvoiceRecordMapper;

    @Autowired
    private PropertiesUtil propertiesUtil;

    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;

    private static final Integer commonInvoiceType = 3;//普票类型是3
    private static final Integer vatInvoiceType = 2;//增票类型是2

    public RemoteResult ifExchangeVatInvoice(String orderCode){
        LOGGER.info("ifExchangeInvoice Start 参数 : orderCode="+orderCode);
        RemoteResult remoteResult = new RemoteResult();
        try {
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();
            if (orderStatus==3){
                remoteResult.setResultMsg("该订单已发货，不允许换票！");
                remoteResult.setResultCode(InvoiceResultCode.FAIL);
            }
            remoteResult.setSuccess(true);
            remoteResult.setResultMsg("可以换票！");
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("ifExchangeInvoice End返回值 : "+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //换发票
    @Override
    public RemoteResult exchangeInvoice(String itCode, String lenovoId, String orderCode, Integer shopid,
                                        Integer oldInvoiceId, String oldInvoiceTitle, Integer oldInvoiceType,
                                        Integer newInvoiceId, String newInvoiceTitle, Integer newInvoiceType,
                                        String taxNo, String bankName, String bankNo, String address, String phone,
                                        String faid,String faType) {
        LOGGER.info("exchangeInvoice参数==itCode="+itCode+";lenovoId="+lenovoId+";orderCode="+orderCode+";shopid="+shopid
                +";oldInvoiceId="+oldInvoiceId+";oldInvoiceTitle="+oldInvoiceTitle+";oldInvoiceType="+oldInvoiceType
                +";newInvoiceId="+newInvoiceId+";newInvoiceTitle="+newInvoiceTitle+";newInvoiceType="+newInvoiceType
                +";taxNo="+taxNo+";bankName="+bankName+";bankNo="+bankNo+";address="+address+";phone="+phone
                +";faid="+faid+";faType="+faType);

        RemoteResult remoteResult = new RemoteResult();
        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus==3){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("exchangeInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //判断换票类型
                if (newInvoiceType==3){
                    //添加新得普票
                    CommonInvoiceServiceImpl commonInvoiceService = new CommonInvoiceServiceImpl();
                    RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(lenovoId, newInvoiceTitle, shopid,itCode);
                    if (!remoteResult1.isSuccess()){
                        //添加失败
                        remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                        remoteResult.setResultMsg("添加新普票失败");
                        LOGGER.info("exchangeInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                        return remoteResult;
                    }
                }else if (newInvoiceType==2){
                    //添加增票
                    InvoiceApiServiceImpl invoiceApiServiceImpl = new InvoiceApiServiceImpl();
                    Tenant tenant = new Tenant();
                    tenant.setShopId(shopid);
                    AddVatInvoiceInfoParam param = new AddVatInvoiceInfoParam();
                    param.setLenovoId(lenovoId);
                    param.setShopId(shopid);
                    param.setCustomerName(newInvoiceTitle);
                    param.setTaxNo(taxNo);
                    param.setBankName(bankName);
                    param.setAccountNo(bankNo);
                    param.setAddress(address);
                    param.setPhoneNo(phone);
                    param.setFaid(faid);
                    param.setFaType(faType);
                    RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiServiceImpl.addVatInvoiceInfo(param, tenant);
                    if (!remoteResult1.isSuccess()){
                        //添加失败
                        remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                        remoteResult.setResultMsg("添加新增票失败");
                        LOGGER.info("exchangeInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                        return remoteResult;
                    }
                }
                //添加成功，再次判断订单状态
                VathrowBtcp vathrowBtcp2 = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
                int orderStatus2 = vathrowBtcp2.getOrderStatus();
                String applyId = UUID.randomUUID().toString().replace("-","");
                if (orderStatus2==3){
                    //订单已发货，不能进行换票操作
                    remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                    remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                    LOGGER.info("exchangeInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }else if (orderStatus2==1){
                    //订单未抛单-已支付，直接修改订单
                    Invoice invoice = new Invoice();
                    invoice.setOrderId(Long.parseLong(orderCode));
                    invoice.setType(newInvoiceType);
                    invoice.setTitle(newInvoiceTitle);
                    invoice.setTaxpayerIdentity(taxNo);
                    invoice.setDepositBank(bankName);
                    invoice.setBankNo(bankNo);
                    invoice.setRegisterAddress(address);
                    invoice.setRegisterPhone(phone);

                    LOGGER.info("vatApiOrderCenter-updateInvoice参数=="+JacksonUtil.toJson(invoice));
                    RemoteResult remoteResult1 = vatApiOrderCenter.updateInvoice(invoice);
                    LOGGER.info("vatApiOrderCenter-updateInvoice返回值=="+JacksonUtil.toJson(remoteResult1));
                    if (remoteResult1.isSuccess()){
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票成功！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setOldInvoiceId(oldInvoiceId);
                            record.setOldInvoiceType(oldInvoiceType);
                            record.setOldInvoiceTitle(oldInvoiceTitle);
                            record.setNewInvoiceId(newInvoiceId);
                            record.setNewInvoiceType(newInvoiceType);
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setTaxNo(taxNo);
                            record.setBankName(bankName);
                            record.setBankNo(bankNo);
                            record.setAddress(address);
                            record.setPhone(phone);
                            record.setState(1);
                            record.setExchangeTime(new Date());

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i==0){
                                LOGGER.error("添加换票记录失败==参数=="+JacksonUtil.toJson(record));
                            }
                        }catch (Exception e){
                            LOGGER.error("添加换票记录失败==参数=="+JacksonUtil.toJson(record)+e.getMessage(),e);
                        }
                    }else {
                        remoteResult.setResultCode(InvoiceResultCode.UPDATEORDERFAIL);
                        remoteResult.setResultMsg("换票失败，修改订单失败");
                    }
                }else if (orderStatus2==2){
                    //订单已抛单-未发货，修改BTCP
                    String invoiceType = "";
                    if (newInvoiceType==2){
                        invoiceType = "Z";
                    }else {
                        invoiceType = "P";
                    }
                    String xml = "<InvoiceEditUnit>";
                    xml += "<ApplyId>"+applyId+"</ApplyId>"; //唯一标识，
                    xml += "<CID>GM</CID>"; //官网是GM
                    xml += "<BTCPSO>"+vathrowBtcp2.getOutid()+"</BTCPSO>";
                    xml += "<InvoiceType>"+invoiceType+"</InvoiceType>"; //普票P，增票Z，电子票D
                    xml += "<invoiceTitle>"+newInvoiceTitle+"</invoiceTitle>";
                    xml += "<UpdatedBy>"+itCode+"</UpdatedBy>";
                    xml += "</InvoiceEditUnit>";

                    String data_digest = MD5.sign(xml, propertiesUtil.getExchangeinvoicekey(), "utf-8");
                    //String data_digest = MD5.sign(xml, "abc123", "utf-8");
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("xml", xml);
                    paramMap.put("cid", "officialportal");
                    paramMap.put("data_digest", data_digest);

                    NetWorkWrapperUtil net = new NetWorkWrapperUtil();

                    LOGGER.info("throwBTCP参数=="+JacksonUtil.toJson(paramMap));
                    String resposeData = net.requestData(propertiesUtil.getExchangeinvoiceurl(), paramMap);
                    LOGGER.info("throwBTCP返回值=="+resposeData);
                    //String resposeData = net.requestData("http://10.120.23.236:8080/btcpws/ChangeInvoiceTypeTitle", paramMap);
                    Map<String, String> map = new HashMap<String, String>();
                    map = XMLUtil.parseXml(resposeData);
                    String resCode = map.get("Code");
                    String message = map.get("Message");

                    if (resCode!=null && "200".equals(resCode)){
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票请求已提交！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setOldInvoiceId(oldInvoiceId);
                            record.setOldInvoiceType(oldInvoiceType);
                            record.setOldInvoiceTitle(oldInvoiceTitle);
                            record.setNewInvoiceId(newInvoiceId);
                            record.setNewInvoiceType(newInvoiceType);
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setTaxNo(taxNo);
                            record.setBankName(bankName);
                            record.setBankNo(bankNo);
                            record.setAddress(address);
                            record.setPhone(phone);
                            record.setState(1);
                            record.setExchangeTime(new Date());

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i==0){
                                LOGGER.error("添加换票记录失败==参数=="+JacksonUtil.toJson(record));
                            }
                        }catch (Exception e){
                            LOGGER.error("添加换票记录失败==参数=="+JacksonUtil.toJson(record)+e.getMessage(),e);
                        }
                    }else {
                        remoteResult.setResultCode(InvoiceResultCode.THROWBTCPFAIL);
                        remoteResult.setResultMsg("换票失败，抛BTCP失败");
                    }
                }
            }
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error("换票失败=="+e.getMessage(),e);
        }
        LOGGER.info("exchangeInvoice返回值=="+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //BTCP回调接口
    public RemoteResult BTCPCallback(String applyId,String code,String message){
        LOGGER.info("BTCPCallback参数==applyId="+applyId+";code="+code+";message="+message);
        RemoteResult remoteResult = new RemoteResult();
        try {
            ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
            if (code!=null && "200".equals(code)){
                try {
                    record.setId(applyId);
                    record.setState(2);
                    int i = exchangeInvoiceRecordMapper.updateExchangeInvoiceRecord(record);
                    if (i==0){
                        LOGGER.error("换票成功，换票记录状态修改失败！"+JacksonUtil.toJson(record));
                    }
                }catch (Exception e){
                    LOGGER.error("换票成功，换票记录状态修改失败！"+JacksonUtil.toJson(record));
                }
                //换票成功，调用订单修改接口，并修改换票记录状态
                record = exchangeInvoiceRecordMapper.getExchangeInvoiceRecord(applyId);
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(record.getOrderCode()));
                invoice.setType(record.getNewInvoiceType());
                invoice.setTitle(record.getNewInvoiceTitle());
                invoice.setTaxpayerIdentity(record.getTaxNo());
                invoice.setDepositBank(record.getBankName());
                invoice.setBankNo(record.getBankNo());
                invoice.setRegisterAddress(record.getAddress());
                invoice.setRegisterPhone(record.getPhone());

                LOGGER.info("BTCPCallback-updateInvoice参数==" + JacksonUtil.toJson(invoice));
                RemoteResult remoteResult1 = vatApiOrderCenter.updateInvoice(invoice);
                LOGGER.info("BTCPCallback-updateInvoice返回值=="+JacksonUtil.toJson(remoteResult1));
                if (!remoteResult1.isSuccess()){
                    //修改订单失败，写入数据库
                    UpdateInvoiceInOrderParams params = new UpdateInvoiceInOrderParams();
                    params.setOrderCode(invoice.getOrderId());
                    params.setInvoiceType(invoice.getType());
                    params.setInvoiceTitle(invoice.getTitle());
                    params.setTaxNo(invoice.getTaxpayerIdentity());
                    params.setBankName(invoice.getDepositBank());
                    params.setBankNo(invoice.getBankNo());
                    params.setAddress(invoice.getRegisterAddress());
                    params.setPhone(invoice.getRegisterPhone());
                    params.setCreateTime(new Date());

                    int i = exchangeInvoiceRecordMapper.addErrorUpdateOrder(params);
                    if (i==0){
                        LOGGER.info("修改订单失败，添加数据库错误记录失败！"+JacksonUtil.toJson(params));
                    }
                }
            }else {
                try {
                    record.setId(applyId);
                    record.setState(3);
                    int i = exchangeInvoiceRecordMapper.updateExchangeInvoiceRecord(record);
                    if (i==0){
                        LOGGER.error("换票失败，换票记录状态修改失败！"+JacksonUtil.toJson(record));
                    }
                }catch (Exception e){
                    LOGGER.error("换票失败，换票记录状态修改失败！"+JacksonUtil.toJson(record));
                }
            }
        }catch (Exception e){
            LOGGER.error("BTCPCallback修改订单出现异常"+applyId+e.getMessage(),e);
        }
        remoteResult.setResultMsg("接收成功");
        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
        remoteResult.setSuccess(true);
        LOGGER.info("BTCPCallback返回值=="+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }


    //普票换普票
    @Override
    public RemoteResult commonToCommon(String orderCode, Integer shopid, String lenovoId, String invoiceTitle,String itCode,Integer oldInvoiceId,String oldInvoiceTitle) {
        LOGGER.info("commonToCommon Start参数 : orderCode="+orderCode+";shopid="+shopid+";lenovoId="+lenovoId+";invoiceTitle="+invoiceTitle+";itCode="+itCode+";oldInvoiceId="+oldInvoiceId+";oldInvoiceTitle="+oldInvoiceTitle);

        RemoteResult remoteResult = new RemoteResult();

        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus==1){
                //订单已支付
                //添加新得普票
                CommonInvoiceServiceImpl commonInvoiceService = new CommonInvoiceServiceImpl();
                RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(lenovoId, invoiceTitle, shopid,itCode);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新普票失败");
                    LOGGER.info("commonToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //发票修改成功，添加换票记录
                CommonInvoice t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, commonInvoiceType, commonInvoiceType, t.getId(), invoiceTitle, oldInvoiceId, oldInvoiceTitle);

                //调用订单接口，修改发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(invoiceTitle);
                invoice.setType(commonInvoiceType);

                //修改发票之前，再判断一下订单状态
                VathrowBtcp vathrowBtcp2 = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
                int orderStatus2 = vathrowBtcp2.getOrderStatus();
                if (orderStatus2==1){
                    LOGGER.info("修改订单之前的订单状态 : "+JacksonUtil.toJson(vathrowBtcp2));
                    updateInvoiceAtOrder(invoice);
                }else if (orderStatus2==2){
                    //调用BTCP接口修改发票信息
                    LOGGER.info("调用BTCP接口时的订单状态 : "+JacksonUtil.toJson(vathrowBtcp2));
                    String applyId = UUID.randomUUID().toString().replace("-","");
                    updateInvoiceAtBTCP(vathrowBtcp2.getOutid(), itCode, invoiceTitle, commonInvoiceType,applyId);
                }
            } else if (orderStatus == 2) {
                //订单未发货
                //添加新得普票
                CommonInvoiceServiceImpl commonInvoiceService = new CommonInvoiceServiceImpl();
                RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(lenovoId, invoiceTitle, shopid,itCode);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新普票失败");
                    LOGGER.info("commonToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //发票修改成功，添加换票记录
                CommonInvoice t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, commonInvoiceType, commonInvoiceType, t.getId(), invoiceTitle, oldInvoiceId, oldInvoiceTitle);
                //调用BTCP接口修改发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(invoiceTitle);
                invoice.setType(commonInvoiceType);
                String applyId = UUID.randomUUID().toString().replace("-","");
                updateInvoiceAtBTCP(vathrowBtcp.getOutid(), itCode, invoiceTitle, commonInvoiceType,applyId);

            }else if (orderStatus==3){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("commonToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //订单状态错误
                remoteResult.setResultCode(InvoiceResultCode.ORDERSTATUSFAIL);
                remoteResult.setResultMsg("订单状态错误");
                LOGGER.info("commonToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("换票请求已提交！");
            remoteResult.setSuccess(true);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("commonToCommon End返回值 : "+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //增票换普票
    @Override
    public RemoteResult vatToCommon(String orderCode, Integer shopid, String lenovoId, String invoiceTitle,String itCode,Integer oldInvoiceId,String oldInvoiceTitle) {
        LOGGER.info("vatToCommon Start参数 : orderCode="+orderCode+";shopid="+shopid+";lenovoId="+lenovoId+";invoiceTitle="+invoiceTitle+";itCode="+itCode+";oldInvoiceId="+oldInvoiceId+";oldInvoiceTitle="+oldInvoiceTitle);

        RemoteResult remoteResult = new RemoteResult();

        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus==1){
                //订单已支付
                //添加新得普票
                CommonInvoiceServiceImpl commonInvoiceService = new CommonInvoiceServiceImpl();
                RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(lenovoId, invoiceTitle, shopid,itCode);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新普票失败");
                    LOGGER.info("vatToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //发票修改成功，添加换票记录
                CommonInvoice t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, vatInvoiceType, commonInvoiceType, t.getId(), invoiceTitle, oldInvoiceId, oldInvoiceTitle);
                //调用订单接口，修改发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(invoiceTitle);
                invoice.setType(commonInvoiceType);

                updateInvoiceAtOrder(invoice);
            }else if (orderStatus==2){
                //订单未发货
                //添加新得普票
                CommonInvoiceServiceImpl commonInvoiceService = new CommonInvoiceServiceImpl();
                RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(lenovoId, invoiceTitle, shopid,itCode);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新普票失败");
                    LOGGER.info("vatToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //发票修改成功，添加换票记录
                CommonInvoice t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, vatInvoiceType, commonInvoiceType, t.getId(), invoiceTitle, oldInvoiceId, oldInvoiceTitle);
                //调用BTCP接口修改发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(invoiceTitle);
                invoice.setType(commonInvoiceType);
                String applyId = UUID.randomUUID().toString().replace("-","");

                updateInvoiceAtBTCP(vathrowBtcp.getOutid(),itCode,invoiceTitle,commonInvoiceType,applyId);
            }else if (orderStatus==3){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("vatToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //订单状态错误
                remoteResult.setResultCode(InvoiceResultCode.ORDERSTATUSFAIL);
                remoteResult.setResultMsg("订单状态错误");
                LOGGER.info("vatToCommon End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("换票成功");
            remoteResult.setSuccess(true);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("vatToCommon End返回值 : "+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //普票换增票
    @Override
    public RemoteResult commonToVat(String orderCode,String itCode,Integer oldInvoiceId,String oldInvoiceTitle,AddVatInvoiceInfoParam
        param) {
        LOGGER.info("commonToVat Start参数 : orderCode="+orderCode+";itCode="+itCode+";oldInvoiceId="+oldInvoiceId+";oldInvoiceTitle"+oldInvoiceTitle+";param="+JacksonUtil.toJson(param));

        RemoteResult remoteResult = new RemoteResult();

        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus==1){
                //订单已支付
                //判断增票是否存在
                InvoiceApiServiceImpl invoiceApiServiceImpl = new InvoiceApiServiceImpl();
                Tenant tenant = new Tenant();
                tenant.setShopId(param.getShopId());
                RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiServiceImpl.addVatInvoiceInfo(param, tenant);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                    remoteResult.setResultMsg("添加新增票失败");
                    LOGGER.info("commonToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //发票修改成功，存储换票记录
                AddVatInvoiceInfoResult t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, commonInvoiceType, vatInvoiceType,(int)t.getVatInvoiceId(),t.getCustomerName(),oldInvoiceId,oldInvoiceTitle);
                //发票修改成功，调用订单接口，修改订单中的发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(param.getCustomerName());
                invoice.setType(vatInvoiceType);
                invoice.setTaxpayerIdentity(param.getTaxNo());
                invoice.setDepositBank(param.getBankName());
                invoice.setBankNo(param.getAccountNo());
                invoice.setRegisterAddress(param.getAddress());
                invoice.setRegisterPhone(param.getPhoneNo());

                updateInvoiceAtOrder(invoice);
            }else if (orderStatus==2){
                //订单未发货
                //判断增票是否存在
                InvoiceApiServiceImpl invoiceApiServiceImpl = new InvoiceApiServiceImpl();
                Tenant tenant = new Tenant();
                tenant.setShopId(param.getShopId());
                RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiServiceImpl.addVatInvoiceInfo(param, tenant);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                    remoteResult.setResultMsg("添加新增票失败");
                    LOGGER.info("commonToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //发票修改成功，存储换票记录
                AddVatInvoiceInfoResult t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, commonInvoiceType, vatInvoiceType,(int)t.getVatInvoiceId(),t.getCustomerName(),oldInvoiceId,oldInvoiceTitle);
                //发票修改成功，调用BTCP接口修改发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(param.getCustomerName());
                invoice.setType(vatInvoiceType);
                invoice.setTaxpayerIdentity(param.getTaxNo());
                invoice.setDepositBank(param.getBankName());
                invoice.setBankNo(param.getAccountNo());
                invoice.setRegisterAddress(param.getAddress());
                invoice.setRegisterPhone(param.getPhoneNo());
                String applyId = UUID.randomUUID().toString().replace("-","");

                updateInvoiceAtBTCP(vathrowBtcp.getOutid(),itCode,param.getCustomerName(),vatInvoiceType,applyId);
            }else if (orderStatus==3){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("commonToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //订单状态错误
                remoteResult.setResultCode(InvoiceResultCode.ORDERSTATUSFAIL);
                remoteResult.setResultMsg("订单状态错误");
                LOGGER.info("commonToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("换票成功");
            remoteResult.setSuccess(true);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("commonToVat End返回值 : "+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //增票换增票
    @Override
    public RemoteResult vatToVat(String orderCode,String itCode,Integer oldInvoiceId,String oldInvoiceTitle,AddVatInvoiceInfoParam param) {
        LOGGER.info("vatToVat Start参数 : orderCode="+orderCode+";itCode="+itCode+";oldInvoiceId="+oldInvoiceId+";oldInvoiceTitle"+oldInvoiceTitle+";param="+JacksonUtil.toJson(param));

        RemoteResult remoteResult = new RemoteResult();

        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus==1){
                //订单已支付
                //判断增票是否存在
                InvoiceApiServiceImpl invoiceApiServiceImpl = new InvoiceApiServiceImpl();
                Tenant tenant = new Tenant();
                tenant.setShopId(param.getShopId());
                RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiServiceImpl.addVatInvoiceInfo(param,tenant);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                    remoteResult.setResultMsg("添加新增票失败");
                    LOGGER.info("vatToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //修改成功，存储换票记录
                AddVatInvoiceInfoResult t = remoteResult1.getT();
                addExchangeInvoiceRecord(itCode, orderCode, vatInvoiceType, vatInvoiceType,(int)t.getVatInvoiceId(),t.getCustomerName(),oldInvoiceId,oldInvoiceTitle);
                //修改成功，调用订单接口修改订单中的发票信息
                Invoice invoice = new Invoice();
                invoice.setOrderId(Long.parseLong(orderCode));
                invoice.setTitle(param.getCustomerName());
                invoice.setType(vatInvoiceType);
                invoice.setTaxpayerIdentity(param.getTaxNo());
                invoice.setDepositBank(param.getBankName());
                invoice.setBankNo(param.getAccountNo());
                invoice.setRegisterAddress(param.getAddress());
                invoice.setRegisterPhone(param.getPhoneNo());

                updateInvoiceAtOrder(invoice);
            }else if (orderStatus==2){
                //订单未发货，判断订单的抛送状态
                int throwingStatus = vathrowBtcp.getThrowingStatus();
                if (throwingStatus==0 || throwingStatus==4){
                    //订单未抛送或者抛送失败
                    //判断增票是否存在
                    InvoiceApiServiceImpl invoiceApiServiceImpl = new InvoiceApiServiceImpl();
                    Tenant tenant = new Tenant();
                    tenant.setShopId(param.getShopId());
                    RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiServiceImpl.addVatInvoiceInfo(param, tenant);
                    if (!remoteResult1.isSuccess()){
                        //添加失败
                        remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                        remoteResult.setResultMsg("添加新增票失败");
                        LOGGER.info("vatToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                        return remoteResult;
                    }
                    //修改成功，存储换票记录
                    AddVatInvoiceInfoResult t = remoteResult1.getT();
                    addExchangeInvoiceRecord(itCode, orderCode, vatInvoiceType, vatInvoiceType,(int)t.getVatInvoiceId(),t.getCustomerName(),oldInvoiceId,oldInvoiceTitle);
                    //发票修改成功，调用BTCP接口修改发票信息
                    Invoice invoice = new Invoice();
                    invoice.setOrderId(Long.parseLong(orderCode));
                    invoice.setTitle(param.getCustomerName());
                    invoice.setType(vatInvoiceType);
                    invoice.setTaxpayerIdentity(param.getTaxNo());
                    invoice.setDepositBank(param.getBankName());
                    invoice.setBankNo(param.getAccountNo());
                    invoice.setRegisterAddress(param.getAddress());
                    invoice.setRegisterPhone(param.getPhoneNo());
                    String applyId = UUID.randomUUID().toString().replace("-","");

                    updateInvoiceAtBTCP(vathrowBtcp.getOutid(),itCode,param.getCustomerName(),vatInvoiceType,applyId);
                }else if (throwingStatus==1 || throwingStatus==2 || throwingStatus==3){
                    //订单不能抛送或者已抛送，不能进行换票操作
                    remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE_THROWORDER);
                    remoteResult.setResultMsg("订单不能抛送或者已抛送，不能进行换票操作");
                    LOGGER.info("vatToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }else {
                    //订单状态错误
                    remoteResult.setResultCode(InvoiceResultCode.ORDERTHROWSTATUSFAIL);
                    remoteResult.setResultMsg("订单抛送状态错误");
                    LOGGER.info("vatToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
            }else if (orderStatus==3){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("vatToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //订单状态错误
                remoteResult.setResultCode(InvoiceResultCode.ORDERSTATUSFAIL);
                remoteResult.setResultMsg("订单状态错误");
                LOGGER.info("vatToVat End返回值 : " + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("换票成功");
            remoteResult.setSuccess(true);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("vatToVat End返回值 : "+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    public static void main(String[] args) {
        String BTCPSO = "N123456789";
        String itCode = "shaoyh2";
        String invoiceTitle = "联想(北京北研)";
        Integer type = 3;
        String applyId = "shaoyh2";
        updateInvoiceAtBTCP(BTCPSO, itCode, invoiceTitle, type, applyId);
    }

    //修改完发票，调用BTCP的发票修改接口
    public static void updateInvoiceAtBTCP(String BTCPSO,String itCode,String invoiceTitle,Integer type,String applyId) {
        try {
            //LOGGER.info("throwBTCP Start参数 : ");
            Map<String, String> map = new HashMap<String, String>();
            String invoiceType = "";
            if (type==2){
                invoiceType = "Z";
            }else {
                invoiceType = "P";
            }
            String xml = "";
            String context = "<InvoiceEditUnit>";

            context += "<ApplyId>"+applyId+"</ApplyId>"; //自己生成一个标识，保证每次修改都不一样，如果修改失败，多次请求的标识应该相同
            context += "<CID>GM</CID>"; //官网是GM
            context += "<BTCPSO>"+BTCPSO+"</BTCPSO>";
            context += "<InvoiceType>"+invoiceType+"</InvoiceType>"; //普票P，增票Z，电子票D
            context += "<invoiceTitle>"+invoiceTitle+"</invoiceTitle>";
            context += "<UpdatedBy>"+itCode+"</UpdatedBy>";

            context += "</InvoiceEditUnit>";

            //xml = "<InvoiceEditList>"+context+"</InvoiceEditList>";
            xml = context;

            System.out.println(xml);

            //LOGGER.info("POST to BTCP  XML = " + xml);

            //key=abc123
            //String data_digest = MD5.sign(xml, propertiesUtil.getExchangeinvoicekey(), "utf-8");
            String data_digest = MD5.sign(xml, "abc123", "utf-8");
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("xml", xml);
            paramMap.put("cid", "officialportal");
            paramMap.put("data_digest", data_digest);

            NetWorkWrapperUtil net = new NetWorkWrapperUtil();

            //http://10.120.23.236:8080/btcpws/ChangeInvoiceTypeTitle
            String resposeData = net.requestData("http://10.120.23.236:8080/btcpws/ChangeInvoiceTypeTitle", paramMap);
            //String resposeData = net.requestData(propertiesUtil.getExchangeinvoiceurl(), paramMap);
            map = XMLUtil.parseXml(resposeData);

            System.out.println(resposeData);

            LOGGER.info("BTCP to POST result = [" + resposeData + "], map=[" + map + "]");

            String resCode = map.get("Code");
            String message = map.get("Message");

            if (resCode!=null && resCode.equals("200")){
                // TODO 换票申请提交成功，返回信息
            }else {
                //换票失败，返回错误信息
            }
        } catch (Exception e) {
            //TODO 换票失败，返回错误信息
            LOGGER.error(e.getMessage(), e);
        }
    }

    //BTCP修改成功，回调接口


    //调用订单接口，修改订单中的发票信息
    public void updateInvoiceAtOrder(Invoice invoice){
        try {
            LOGGER.info("updateInvoiceAtOrder Start参数 : invoice=" + JacksonUtil.toJson(invoice));
            RemoteResult remoteResult2 = vatApiOrderCenter.updateInvoice(invoice);
            /*if (!remoteResult2.isSuccess()){
                //修改订单失败，写入错误数据库
                UpdateInvoiceInOrderParams params = new UpdateInvoiceInOrderParams();
                params.setOrderCode(invoice.getOrderId());
                params.setInvoiceTitle(invoice.getTitle());
                params.setInvoiceType(invoice.getType());
                params.setTaxNo(invoice.getTaxpayerIdentity());
                params.setBankName(invoice.getDepositBank());
                params.setAddress(invoice.getRegisterAddress());
                params.setPhone(invoice.getRegisterPhone());
                params.setCreateTime(new Date());

                int i = exchangeInvoiceRecordMapper.addErrorUpdateOrder(params);
                if (i==0){
                    LOGGER.info("修改订单失败后，参数写入数据库失败！"+JacksonUtil.toJson(invoice));
                }
            }*/
        }catch (Exception e){
            //修改订单出现异常，写入错误数据库
            //修改订单失败，写入错误数据库
            /*UpdateInvoiceInOrderParams params = new UpdateInvoiceInOrderParams();
            params.setOrderCode(invoice.getOrderId());
            params.setInvoiceTitle(invoice.getTitle());
            params.setInvoiceType(invoice.getType());
            params.setTaxNo(invoice.getTaxpayerIdentity());
            params.setDepositBank(invoice.getDepositBank());
            params.setAddress(invoice.getRegisterAddress());
            params.setPhone(invoice.getRegisterPhone());
            params.setType(type);
            int i = exchangeInvoiceRecordMapper.addErrorUpdateOrder(params);
            if (i==0){
                LOGGER.info("修改订单失败后，参数写入数据库失败！"+JacksonUtil.toJson(invoice));
            }*/
            LOGGER.error(e.getMessage(),e);
        }
    }

    //添加换票记录
    public void addExchangeInvoiceRecord(String itCode,String orderCode,Integer oldInvoiceType,Integer newInvoiceType,Integer newInvoiceId,String newInvoiceTitle,Integer oldInvoiceId,String oldInvoiceTitle){
        try {
            LOGGER.info("addExchangeInvoiceRecord Start参数 : itCode="+itCode+";orderCode="+orderCode+";oldInvoiceType="+oldInvoiceType+";oldInvoiceId="+oldInvoiceId+";oldInvoiceTitle="+oldInvoiceTitle+";newInvoiceType="+newInvoiceType+";newInvoiceId="+newInvoiceId+";newInvoiceTitle="+newInvoiceTitle);
            ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
            record.setItCode(itCode);
            record.setOrderCode(orderCode);
            record.setExchangeTime(new Date());
            record.setOldInvoiceType(oldInvoiceType);
            record.setNewInvoiceType(newInvoiceType);
            record.setNewInvoiceId(newInvoiceId);
            record.setNewInvoiceTitle(newInvoiceTitle);
            record.setOldInvoiceId(oldInvoiceId);
            record.setOldInvoiceTitle(oldInvoiceTitle);

            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
            if (i==0){
                LOGGER.info("addExchangeInvoiceRecord 添加换票记录失败 参数: itCode="+itCode+";orderCode="+orderCode+";oldInvoiceType="+oldInvoiceType+";newInvoiceType="+newInvoiceType);
            }
        }catch (Exception e){
            LOGGER.info("addExchangeInvoiceRecord 添加换票记录失败 参数: itCode="+itCode+";orderCode="+orderCode+";oldInvoiceType="+oldInvoiceType+";newInvoiceType="+newInvoiceType);
            LOGGER.error(e.getMessage(), e);
        }
    }

}
