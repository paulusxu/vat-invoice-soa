package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.ExchangeInvoiceService;
import com.lenovo.invoice.common.utils.*;
import com.lenovo.invoice.dao.ExchangeInvoiceRecordMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
import com.lenovo.invoice.domain.result.AddVatInvoiceInfoResult;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.m2.arch.framework.domain.*;
import com.lenovo.m2.ordercenter.soa.api.model.forward.InvoiceChangeApi;
import com.lenovo.m2.ordercenter.soa.api.query.order.InvoiceQueryService;
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
public class ExchangeInvoiceServiceImpl extends BaseService implements ExchangeInvoiceService{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeInvoiceServiceImpl.class);

    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;

    @Autowired
    private ExchangeInvoiceRecordMapper exchangeInvoiceRecordMapper;

    @Autowired
    private PropertiesUtil propertiesUtil;

    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;

    @Autowired
    private InvoiceQueryService invoiceQueryService;

    private static final Integer commonInvoiceType = 3;//普票类型是3
    private static final Integer vatInvoiceType = 2;//增票类型是2

    //换普票
    @Override
    public RemoteResult exchangeToCommon(String orderCode,String itCode,Integer oldInvoiceType,Integer exchangeType,Integer type,String newInvoiceTitle) {
        LOGGER.info("exchangeToCommon参数==orderCode="+orderCode+";oldInvoiceType="+oldInvoiceType+";itCode="+itCode+";exchangeType="+exchangeType+";type="+type+";newInvoiceTitle="+newInvoiceTitle);

        RemoteResult remoteResult = new RemoteResult();

        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus==3){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //获取老发票信息和订单信息
                RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
                InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
                if (invoiceChangeApi==null){
                    //获取老发票信息和订单信息失败
                    remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                    remoteResult.setResultMsg("获取老发票信息和订单信息失败");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //还未发货，客户换票，首先添加新得普票
                CommonInvoiceServiceImpl commonInvoiceService = new CommonInvoiceServiceImpl();
                RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(invoiceChangeApi.getLenovoId(), newInvoiceTitle, invoiceChangeApi.getShopId(),itCode,type);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新普票失败");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }

                //添加成功，再次判断订单状态
                VathrowBtcp vathrowBtcp2 = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
                int orderStatus2 = vathrowBtcp2.getOrderStatus();
                String applyId = UUID.randomUUID().toString().replace("-","");
                if (orderStatus2==3){
                    //订单已发货，不能进行换票操作
                    remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                    remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }else if (orderStatus2==1){
                    //订单未抛单-已支付，直接修改订单
                    Invoice invoice = new Invoice();
                    invoice.setOrderId(Long.parseLong(orderCode));
                    invoice.setType(commonInvoiceType);
                    invoice.setTitle(newInvoiceTitle);

                    LOGGER.info("vatApiOrderCenter-updateInvoice参数=="+JacksonUtil.toJson(invoice));
                    RemoteResult remoteResult2 = vatApiOrderCenter.updateInvoice(invoice);
                    LOGGER.info("vatApiOrderCenter-updateInvoice返回值=="+JacksonUtil.toJson(remoteResult2));
                    if (remoteResult2.isSuccess()){
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票成功！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            Date date = new Date();

                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi.getOutId());
                            record.setShopid(invoiceChangeApi.getShopId());
                            //换票成功，暂时写3
                            record.setState(3);
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);

                            if (oldInvoiceType==1){
                                //电换普
                                record.setExchangeType(1);
                            }else if (oldInvoiceType==2){
                                //增换普
                                record.setExchangeType(2);
                            }else if (oldInvoiceType==3){
                                //普换普
                                record.setExchangeType(3);
                            }

                            //老发票信息
                            record.setOldType(invoiceChangeApi.getInvoiceHeader());//暂时写死的
                            record.setOldInvoiceId(1);//暂时没有
                            record.setOldInvoiceTitle(invoiceChangeApi.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi.getType());
                            record.setOldTaxNo(invoiceChangeApi.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi.getBankNo());
                            record.setOldAddress(invoiceChangeApi.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi.getRegisterPhone());

                            //新发票信息
                            record.setNewType(type);
                            record.setNewInvoiceId(remoteResult1.getT().getId());
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setNewInvoiceType(commonInvoiceType);

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
                    String xml = "<InvoiceEditUnit>";
                    xml += "<ApplyId>"+applyId+"</ApplyId>"; //唯一标识，
                    xml += "<CID>GM</CID>"; //官网是GM
                    xml += "<BTCPSO>"+invoiceChangeApi.getOutId()+"</BTCPSO>";
                    xml += "<InvoiceType>P</InvoiceType>"; //普票P，增票Z，电子票D
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
                            Date date = new Date();

                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi.getOutId());
                            record.setShopid(invoiceChangeApi.getShopId());
                            //换票成功，暂时写3
                            record.setState(3);
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);

                            if (oldInvoiceType==1){
                                //电换普
                                record.setExchangeType(1);
                            }else if (oldInvoiceType==2){
                                //增换普
                                record.setExchangeType(2);
                            }else if (oldInvoiceType==3){
                                //普换普
                                record.setExchangeType(3);
                            }

                            //老发票信息
                            record.setOldType(invoiceChangeApi.getInvoiceHeader());//暂时写死
                            record.setOldInvoiceId(1);//暂时没有
                            record.setOldInvoiceTitle(invoiceChangeApi.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi.getType());
                            record.setOldTaxNo(invoiceChangeApi.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi.getBankNo());
                            record.setOldAddress(invoiceChangeApi.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi.getRegisterPhone());

                            //新发票信息
                            record.setNewType(type);
                            record.setNewInvoiceId(remoteResult1.getT().getId());
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setNewInvoiceType(commonInvoiceType);

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
        LOGGER.info("exchangeToCommon返回值=="+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //换增票
    @Override
    public RemoteResult exchangeToVat(String orderCode,String itCode,Integer oldInvoiceType,Integer exchangeType,String newInvoiceTitle,
                                      String newTaxNo,String newBankName,String newBankNo,String newAddress,String newPhone) {
        LOGGER.info("exchangeToVat参数==orderCode=" + orderCode + ";oldInvoiceType=" + oldInvoiceType + ";itCode=" + itCode + ";exchangeType=" + exchangeType
                + ";newInvoiceTitle=" + newInvoiceTitle + ";newTaxNo=" + newTaxNo + ";newBankName=" + newBankName + ";newBankNo=" + newBankNo + ";newAddress=" + newAddress + ";newPhone=" + newPhone);

        RemoteResult remoteResult = new RemoteResult();
        try {
            //第一步：获取订单状态
            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();

            if (orderStatus == 3) {
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            } else {
                //获取老发票信息和订单信息
                RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
                InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
                if (invoiceChangeApi==null){
                    //获取老发票信息和订单信息失败
                    remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                    remoteResult.setResultMsg("获取老发票信息和订单信息失败");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //添加增票
                InvoiceApiServiceImpl invoiceApiServiceImpl = new InvoiceApiServiceImpl();
                Tenant tenant = new Tenant();
                tenant.setShopId(invoiceChangeApi.getShopId());
                AddVatInvoiceInfoParam param = new AddVatInvoiceInfoParam();
                param.setLenovoId(invoiceChangeApi.getLenovoId());
                param.setShopId(invoiceChangeApi.getShopId());
                param.setCustomerName(newInvoiceTitle);
                param.setTaxNo(newTaxNo);
                param.setBankName(newBankName);
                param.setAccountNo(newBankNo);
                param.setAddress(newAddress);
                param.setPhoneNo(newPhone);
                param.setFaid(invoiceChangeApi.getFaid());
                param.setFaType(invoiceChangeApi.getFaType()+"");
                RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiServiceImpl.addVatInvoiceInfo(param, tenant);
                if (!remoteResult1.isSuccess()) {
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                    remoteResult.setResultMsg("添加新增票失败");
                    LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }

                //添加成功，再次判断订单状态
                VathrowBtcp vathrowBtcp2 = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
                int orderStatus2 = vathrowBtcp2.getOrderStatus();
                String applyId = UUID.randomUUID().toString().replace("-", "");
                if (orderStatus2 == 3) {
                    //订单已发货，不能进行换票操作
                    remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                    remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                    LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                } else if (orderStatus2 == 1) {
                    //订单未抛单-已支付，直接修改订单
                    Invoice invoice = new Invoice();
                    invoice.setOrderId(Long.parseLong(orderCode));
                    invoice.setType(vatInvoiceType);
                    invoice.setTitle(newInvoiceTitle);
                    invoice.setTaxpayerIdentity(newTaxNo);
                    invoice.setDepositBank(newBankName);
                    invoice.setBankNo(newBankNo);
                    invoice.setRegisterAddress(newAddress);
                    invoice.setRegisterPhone(newPhone);

                    LOGGER.info("vatApiOrderCenter-updateInvoice参数==" + JacksonUtil.toJson(invoice));
                    RemoteResult remoteResult2 = vatApiOrderCenter.updateInvoice(invoice);
                    LOGGER.info("vatApiOrderCenter-updateInvoice返回值==" + JacksonUtil.toJson(remoteResult2));
                    if (remoteResult2.isSuccess()) {
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票成功！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            Date date = new Date();

                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi.getOutId());
                            record.setShopid(invoiceChangeApi.getShopId());
                            //换票成功，暂时写3
                            record.setState(3);
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);

                            if (oldInvoiceType == 1) {
                                //电换增
                                record.setExchangeType(4);
                            } else if (oldInvoiceType == 2) {
                                //增换增
                                record.setExchangeType(5);
                            } else if (oldInvoiceType == 3) {
                                //普换增
                                record.setExchangeType(6);
                            }

                            //老发票信息
                            record.setOldType(invoiceChangeApi.getInvoiceHeader());//暂时写死的
                            record.setOldInvoiceId(1);//暂时没有
                            record.setOldInvoiceTitle(invoiceChangeApi.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi.getType());
                            record.setOldTaxNo(invoiceChangeApi.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi.getBankNo());
                            record.setOldAddress(invoiceChangeApi.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi.getRegisterPhone());

                            //新发票信息
                            record.setNewType(1);//增票只有公司的
                            record.setNewInvoiceId((int) remoteResult1.getT().getVatInvoiceId());
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setNewInvoiceType(vatInvoiceType);
                            record.setNewTaxNo(newTaxNo);
                            record.setNewBankName(newBankName);
                            record.setNewBankNo(newBankNo);
                            record.setNewAddress(newAddress);
                            record.setNewPhone(newPhone);

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i == 0) {
                                LOGGER.error("添加换票记录失败==参数==" + JacksonUtil.toJson(record));
                            }
                        } catch (Exception e) {
                            LOGGER.error("添加换票记录失败==参数==" + JacksonUtil.toJson(record) + e.getMessage(), e);
                        }
                    } else {
                        remoteResult.setResultCode(InvoiceResultCode.UPDATEORDERFAIL);
                        remoteResult.setResultMsg("换票失败，修改订单失败");
                    }
                } else if (orderStatus2 == 2) {
                    //订单已抛单-未发货，修改BTCP
                    String xml = "<InvoiceEditUnit>";
                    xml += "<ApplyId>" + applyId + "</ApplyId>"; //唯一标识，
                    xml += "<CID>GM</CID>"; //官网是GM
                    xml += "<BTCPSO>" + vathrowBtcp2.getOutid() + "</BTCPSO>";
                    xml += "<InvoiceType>Z</InvoiceType>"; //普票P，增票Z，电子票D
                    xml += "<invoiceTitle>" + newInvoiceTitle + "</invoiceTitle>";
                    xml += "<UpdatedBy>" + itCode + "</UpdatedBy>";
                    xml += "</InvoiceEditUnit>";

                    String data_digest = MD5.sign(xml, propertiesUtil.getExchangeinvoicekey(), "utf-8");
                    //String data_digest = MD5.sign(xml, "abc123", "utf-8");
                    Map<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("xml", xml);
                    paramMap.put("cid", "officialportal");
                    paramMap.put("data_digest", data_digest);

                    NetWorkWrapperUtil net = new NetWorkWrapperUtil();

                    LOGGER.info("throwBTCP参数==" + JacksonUtil.toJson(paramMap));
                    String resposeData = net.requestData(propertiesUtil.getExchangeinvoiceurl(), paramMap);
                    LOGGER.info("throwBTCP返回值==" + resposeData);
                    //String resposeData = net.requestData("http://10.120.23.236:8080/btcpws/ChangeInvoiceTypeTitle", paramMap);
                    Map<String, String> map = new HashMap<String, String>();
                    map = XMLUtil.parseXml(resposeData);
                    String resCode = map.get("Code");
                    String message = map.get("Message");

                    if (resCode != null && "200".equals(resCode)) {
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票请求已提交！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            Date date = new Date();

                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi.getOutId());
                            record.setShopid(invoiceChangeApi.getShopId());
                            //换票成功，暂时写3
                            record.setState(3);
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);

                            if (oldInvoiceType == 1) {
                                //电换增
                                record.setExchangeType(4);
                            } else if (oldInvoiceType == 2) {
                                //增换增
                                record.setExchangeType(5);
                            } else if (oldInvoiceType == 3) {
                                //普换增
                                record.setExchangeType(6);
                            }

                            //老发票信息
                            record.setOldType(invoiceChangeApi.getInvoiceHeader());//暂时写死的
                            record.setOldInvoiceId(1);//暂时没有
                            record.setOldInvoiceTitle(invoiceChangeApi.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi.getType());
                            record.setOldTaxNo(invoiceChangeApi.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi.getBankNo());
                            record.setOldAddress(invoiceChangeApi.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi.getRegisterPhone());

                            //新发票信息
                            record.setNewType(1);//增票只有公司的
                            record.setNewInvoiceId((int) remoteResult1.getT().getVatInvoiceId());
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setNewInvoiceType(vatInvoiceType);
                            record.setNewTaxNo(newTaxNo);
                            record.setNewBankName(newBankName);
                            record.setNewBankNo(newBankNo);
                            record.setNewAddress(newAddress);
                            record.setNewPhone(newPhone);

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i == 0) {
                                LOGGER.error("添加换票记录失败==参数==" + JacksonUtil.toJson(record));
                            }
                        } catch (Exception e) {
                            LOGGER.error("添加换票记录失败==参数==" + JacksonUtil.toJson(record) + e.getMessage(), e);
                        }
                    } else {
                        remoteResult.setResultCode(InvoiceResultCode.THROWBTCPFAIL);
                        remoteResult.setResultMsg("换票失败，抛BTCP失败");
                    }
                }
            }
        } catch (Exception e) {
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error("换票失败==" + e.getMessage(), e);
        }
        LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //获取换票记录，加分页
    public RemoteResult<PageModel2<ExchangeInvoiceRecord>> getExchangeInvoiceRecordByPage(PageQuery pageQuery, ExchangeInvoiceRecord exchangeInvoiceRecord){
        LOGGER.info("getExchangeInvoiceRecordByPage参数=="+JacksonUtil.toJson(pageQuery)+";=="+JacksonUtil.toJson(exchangeInvoiceRecord));

        RemoteResult<PageModel2<ExchangeInvoiceRecord>> remoteResult = new RemoteResult<PageModel2<ExchangeInvoiceRecord>>();

        try {
            PageModel<ExchangeInvoiceRecord> pageModel = exchangeInvoiceRecordMapper.getExchangeInvoiceRecordByPage(pageQuery, exchangeInvoiceRecord);
            PageModel2<ExchangeInvoiceRecord> pageModel2 = new PageModel2<ExchangeInvoiceRecord>(pageModel);

            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setT(pageModel2);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getExchangeInvoiceRecordByPage返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //获取换票记录详情
    public RemoteResult<ExchangeInvoiceRecord> getExchangeInvoiceRecord(String id){
        LOGGER.info("getExchangeInvoiceRecord参数==id="+id);

        RemoteResult<ExchangeInvoiceRecord> remoteResult = new RemoteResult<ExchangeInvoiceRecord>();

        try {
            if (StringUtil.isEmpty(id)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误！");
                LOGGER.info("getExchangeInvoiceRecord返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            ExchangeInvoiceRecord exchangeInvoiceRecord = exchangeInvoiceRecordMapper.getExchangeInvoiceRecord(id);

            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setT(exchangeInvoiceRecord);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getExchangeInvoiceRecord返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //校验是否可以换票
    @Override
    public RemoteResult ifExchangeVatInvoice(String orderCode){
        LOGGER.info("ifExchangeInvoice Start 参数 : orderCode="+orderCode);
        RemoteResult remoteResult = new RemoteResult();
        try {
            if (StringUtil.isEmpty(orderCode)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("ifExchangeVatInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
            int orderStatus = vathrowBtcp.getOrderStatus();
            if (orderStatus==3){
                remoteResult.setResultMsg("该订单已发货，不允许换票！");
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
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

    //BTCP回调函数，修改换票记录和订单
    @Override
    public RemoteResult BTCPCallback(String applyId,String code,String message){
        LOGGER.info("BTCPCallback参数==applyId="+applyId+";code="+code+";message="+message);
        RemoteResult remoteResult = new RemoteResult();
        Date date = new Date();
        try {
            ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
            if (code!=null && "200".equals(code)){
                try {
                    record.setId(applyId);
                    record.setState(2);
                    record.setUpdateTime(date);
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
                invoice.setTaxpayerIdentity(record.getNewTaxNo());
                invoice.setDepositBank(record.getNewBankName());
                invoice.setBankNo(record.getNewBankNo());
                invoice.setRegisterAddress(record.getNewAddress());
                invoice.setRegisterPhone(record.getNewPhone());

                LOGGER.info("BTCP通知=修改订单参数==" + JacksonUtil.toJson(invoice));
                RemoteResult remoteResult1 = vatApiOrderCenter.updateInvoice(invoice);
                LOGGER.info("BTCP通知=修改订单返回值=="+JacksonUtil.toJson(remoteResult1));
                /*if (!remoteResult1.isSuccess()){
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
                }*/
            }else {
                try {
                    record.setId(applyId);
                    record.setState(3);
                    record.setUpdateTime(date);
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


    //换发票
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
                    RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(lenovoId, newInvoiceTitle, shopid,itCode,1);
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
                            //RemoteResult<Invoice> invoiceByOrderId = invoiceQueryService.getInvoiceByOrderId(orderCode);


                            //添加换票记录 TODO
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setOldInvoiceId(oldInvoiceId);
                            record.setOldInvoiceType(oldInvoiceType);
                            record.setOldInvoiceTitle(oldInvoiceTitle);
                            record.setNewInvoiceId(newInvoiceId);
                            record.setNewInvoiceType(newInvoiceType);
                            record.setNewInvoiceTitle(newInvoiceTitle);
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
                            //添加换票记录 TODO
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setOldInvoiceId(oldInvoiceId);
                            record.setOldInvoiceType(oldInvoiceType);
                            record.setOldInvoiceTitle(oldInvoiceTitle);
                            record.setNewInvoiceId(newInvoiceId);
                            record.setNewInvoiceType(newInvoiceType);
                            record.setNewInvoiceTitle(newInvoiceTitle);
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

}
