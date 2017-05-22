package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.api.ExchangeInvoiceService;
import com.lenovo.invoice.api.InvoiceApiService;
import com.lenovo.invoice.common.utils.*;
import com.lenovo.invoice.dao.ExchangeInvoiceRecordMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.CommonInvoice;
import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
import com.lenovo.invoice.domain.param.GetVatInvoiceInfoParam;
import com.lenovo.invoice.domain.result.AddVatInvoiceInfoResult;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.m2.arch.framework.domain.*;
import com.lenovo.m2.buy.order.address.api.AreaAddressService;
import com.lenovo.m2.buy.order.address.api.ConsigneeAddressService;
import com.lenovo.m2.buy.order.address.api.param.ConsigneeAddressParam;
import com.lenovo.m2.buy.order.address.api.param.ProvinceParam;
import com.lenovo.m2.ordercenter.soa.api.model.forward.InvoiceChangeApi;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by admin on 2017/3/19.
 */
@Service("exchangeInvoiceService")
public class ExchangeInvoiceServiceImpl extends BaseService implements ExchangeInvoiceService{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeInvoiceServiceImpl.class);
    private static final Logger ERRORLOGGER = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.exchangeInvoice");

    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;

    @Autowired
    private ExchangeInvoiceRecordMapper exchangeInvoiceRecordMapper;

    @Autowired
    private PropertiesUtil propertiesUtil;

    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;

    @Autowired
    private ConsigneeAddressService consigneeAddressService;

    @Autowired
    private AreaAddressService areaAddressService;

    @Autowired
    private CommonInvoiceService commonInvoiceService;

    @Autowired
    private InvoiceApiService invoiceApiService;

    @Autowired
    private VatInvoiceService vatInvoiceService;

    private static final Integer commonInvoiceType = 3;//普票类型是3
    private static final Integer vatInvoiceType = 2;//增票类型是2

    //换普票
    @Override
    public RemoteResult exchangeToCommon(String orderCode,String itCode,Integer oldInvoiceType,Integer exchangeType,Integer type,String newInvoiceTitle) {
        LOGGER.info("exchangeToCommon参数==orderCode="+orderCode+";oldInvoiceType="+oldInvoiceType+";itCode="+itCode+";exchangeType="+exchangeType+";type="+type+";newInvoiceTitle="+newInvoiceTitle);

        RemoteResult remoteResult = new RemoteResult();

        try {
            //判断换票类型
            int changeType;
            if (oldInvoiceType==1){
                changeType = 1;//电换普
            }else if (oldInvoiceType==2){
                changeType = 2;//增换普
            }else if (oldInvoiceType==3){
                changeType = 3;//普换普
            }else {
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            //获取订单信息
            LOGGER.info("获取订单信息==参数=="+orderCode);
            RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
            LOGGER.info("获取订单信息==返回值=="+JacksonUtil.toJson(invoiceChangeApiByOrderId));
            InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
            if (invoiceChangeApi==null){
                //获取订单信息失败
                remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                remoteResult.setResultMsg("获取订单信息失败");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            //0 已支付，未抛单，1 已抛单、未发货，2已发货
            int orderStatus = invoiceChangeApi.getOrderStatus();

            if (orderStatus==2){
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }else {
                //创建当前时间
                Date date = new Date();
                Tenant tenant = new Tenant();
                tenant.setShopId(invoiceChangeApi.getShopId());

                //还未发货，客户换票，首先添加新得普票
                RemoteResult<CommonInvoice> remoteResult1 = commonInvoiceService.addCommonInvoice(invoiceChangeApi.getLenovoId(), newInvoiceTitle, invoiceChangeApi.getShopId(),itCode,type);
                if (!remoteResult1.isSuccess()){
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDCOMMONINVOICEFAIL);
                    remoteResult.setResultMsg("添加新普票失败");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }

                //添加成功，再次判断订单状态
                LOGGER.info("获取订单信息==参数=="+orderCode);
                RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId2 = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
                LOGGER.info("获取订单信息==返回值==" + JacksonUtil.toJson(invoiceChangeApiByOrderId2));
                InvoiceChangeApi invoiceChangeApi2 = invoiceChangeApiByOrderId2.getT();
                if (invoiceChangeApi2==null){
                    //获取订单信息失败
                    remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                    remoteResult.setResultMsg("获取订单信息失败");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //0 已支付，未抛单，1 已抛单、未发货，2已发货
                int orderStatus2 = invoiceChangeApi2.getOrderStatus();
                String applyId = UUID.randomUUID().toString().replace("-","");
                if (orderStatus2==2){
                    //订单已发货，不能进行换票操作
                    remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                    remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }else if (orderStatus2==0) {
                    //订单未抛单-已支付，直接修改订单
                    InvoiceChangeApi invoiceChangeApi1 = new InvoiceChangeApi();
                    invoiceChangeApi1.setOrderId(Long.parseLong(orderCode));
                    invoiceChangeApi1.setOrderStatus(0);
                    invoiceChangeApi1.setType(commonInvoiceType);
                    invoiceChangeApi1.setTitle(newInvoiceTitle);
                    invoiceChangeApi1.setUnits(type);
                    invoiceChangeApi1.setChangeType(changeType);
                    invoiceChangeApi1.setOperator(itCode);
                    invoiceChangeApi1.setIsNeedMerge(0);
                    invoiceChangeApi1.setShopId(invoiceChangeApi2.getShopId());

                    LOGGER.info("修改订单==参数=="+JacksonUtil.toJson(invoiceChangeApi1));
                    RemoteResult remoteResult2 = vatApiOrderCenter.updateInvoice(invoiceChangeApi1);
                    LOGGER.info("修改订单==返回值=="+JacksonUtil.toJson(remoteResult2));
                    if (remoteResult2.isSuccess()){
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票成功！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi2.getOutId());
                            record.setShopid(invoiceChangeApi2.getShopId());
                            record.setState(2);//换票成功，2
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);
                            record.setExchangeType(changeType);
                            record.setLenovoId(invoiceChangeApi2.getLenovoId());

                            //老发票信息
                            record.setOldType(invoiceChangeApi2.getUnits());
                            record.setOldInvoiceId(1);//没有，初始化1
                            record.setOldInvoiceTitle(invoiceChangeApi2.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi2.getType());
                            record.setOldTaxNo(invoiceChangeApi2.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi2.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi2.getBankNo());
                            record.setOldAddress(invoiceChangeApi2.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi2.getRegisterPhone());

                            //新发票信息
                            record.setNewType(type);
                            record.setNewInvoiceId(remoteResult1.getT().getId());
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setNewInvoiceType(commonInvoiceType);

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i<=0){
                                ERRORLOGGER.error("添加换票记录失败==参数=="+i+"=="+ JacksonUtil.toJson(record));
                            }else {
                                LOGGER.info("添加换票记录成功！=="+i);
                            }
                        }catch (Exception e){
                            ERRORLOGGER.error("添加换票记录出现异常==参数==" + JacksonUtil.toJson(record)+"==" + e.getMessage(), e);
                        }
                        try {
                            if (changeType==2){
                                //增换普，将增票和订单的映射记录删除 TODO 改为伪删除
                                VathrowBtcp vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);

                                int i = vathrowBtcpMapper.deleteByOrderCode(orderCode);
                                if (i<=0){
                                    ERRORLOGGER.error("增换普，换票成功，增票和订单映射删除失败！=="+i+"=="+orderCode);
                                }else {
                                    LOGGER.info("增换普，换票成功，增票和订单映射删除成功！=="+i+"=="+JacksonUtil.toJson(vathrowBtcp));
                                }
                            }
                        }catch (Exception e){
                            ERRORLOGGER.error("增换普，换票成功，增票和订单映射删除出现异常！"+orderCode+"=="+e.getMessage(),e);
                        }
                    }else if ("9003".equals(remoteResult2.getResultCode())){
                        remoteResult.setResultCode(InvoiceResultCode.UPDATEORDERFAIL);
                        remoteResult.setResultMsg("该订单正在抛单，请五分钟后再试！");
                    }else {
                        remoteResult.setResultCode(InvoiceResultCode.UPDATEORDERFAIL);
                        remoteResult.setResultMsg("换票失败，修改订单失败");
                    }
                }else if (orderStatus2==1){
                    //订单已抛单-未发货，修改BTCP
                    String xml = "<InvoiceEditUnit>";
                    xml += "<ApplyId>"+applyId+"</ApplyId>"; //唯一标识，
                    xml += "<CID>GM</CID>"; //官网是GM
                    xml += "<BTCPSO>"+invoiceChangeApi2.getOutId()+"</BTCPSO>";
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

                    LOGGER.info("调用BTCP参数=="+JacksonUtil.toJson(paramMap));
                    String resposeData = net.requestData(propertiesUtil.getExchangeinvoiceurl(), paramMap);
                    LOGGER.info("调用BTCP返回值=="+resposeData);
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
                            record.setBTCPOrderCode(invoiceChangeApi2.getOutId());
                            record.setShopid(invoiceChangeApi2.getShopId());
                            record.setState(1);//换票中，1
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);
                            record.setExchangeType(changeType);
                            record.setLenovoId(invoiceChangeApi2.getLenovoId());

                            //老发票信息
                            record.setOldType(invoiceChangeApi2.getUnits());
                            record.setOldInvoiceId(1);//没有，初始化为1
                            record.setOldInvoiceTitle(invoiceChangeApi2.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi2.getType());
                            record.setOldTaxNo(invoiceChangeApi2.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi2.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi2.getBankNo());
                            record.setOldAddress(invoiceChangeApi2.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi2.getRegisterPhone());

                            //新发票信息
                            record.setNewType(type);
                            record.setNewInvoiceId(remoteResult1.getT().getId());
                            record.setNewInvoiceTitle(newInvoiceTitle);
                            record.setNewInvoiceType(commonInvoiceType);

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i<=0){
                                ERRORLOGGER.error("添加换票记录失败==参数=="+i+"=="+JacksonUtil.toJson(record));
                            }else {
                                LOGGER.info("添加换票记录成功！=="+i);
                            }
                        }catch (Exception e){
                            ERRORLOGGER.error("添加换票记录出现异常==参数=="+JacksonUtil.toJson(record)+"=="+e.getMessage(),e);
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
                                      String newTaxNo,String newBankName,String newBankNo,String newAddress,String newPhone,
                                      String name,String province,String city,String county,String address2,String phone2,String zip) {
        LOGGER.info("exchangeToVat参数==orderCode=" + orderCode + ";oldInvoiceType=" + oldInvoiceType + ";itCode=" + itCode + ";exchangeType=" + exchangeType
                + ";newInvoiceTitle=" + newInvoiceTitle + ";newTaxNo=" + newTaxNo + ";newBankName=" + newBankName + ";newBankNo=" + newBankNo + ";newAddress=" + newAddress + ";newPhone=" + newPhone+
                ";name="+name+";province="+province+";city="+city+";county="+county+";address2="+address2+";phone2="+phone2+";zip="+zip);

        RemoteResult remoteResult = new RemoteResult();
        try {
            //判断换票类型
            int changeType;
            if (oldInvoiceType==1){
                changeType = 4;//电换增
            }else if (oldInvoiceType==2){
                changeType = 5;//增换增
            }else if (oldInvoiceType==3){
                changeType = 6;//普换增
            }else {
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            //获取订单信息
            LOGGER.info("获取订单信息==参数=="+orderCode);
            RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
            LOGGER.info("获取订单信息==返回值=="+JacksonUtil.toJson(invoiceChangeApiByOrderId));
            InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
            if (invoiceChangeApi==null){
                //获取订单信息失败
                remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                remoteResult.setResultMsg("获取订单信息失败");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            //0 已支付，未抛单，1 已抛单、未发货，2已发货
            int orderStatus = invoiceChangeApi.getOrderStatus();
            if (orderStatus == 2) {
                //订单已发货，不能进行换票操作
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            } else {
                //当前时间
                Date date = new Date();
                //创建国际化对象，初始化shopId
                Tenant tenant = new Tenant();
                tenant.setShopId(invoiceChangeApi.getShopId());
                //添加增票
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
                RemoteResult<AddVatInvoiceInfoResult> remoteResult1 = invoiceApiService.addVatInvoiceInfoForChange(param, tenant);
                if (!remoteResult1.isSuccess()) {
                    //添加失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDVATINVOICEFAIL);
                    remoteResult.setResultMsg("添加新增票失败");
                    LOGGER.info("exchangeToVat返回值==添加新增票失败==" + JacksonUtil.toJson(remoteResult1));
                    return remoteResult;
                }

                //根据省份获取省编号
                RemoteResult<String> remoteResult5 = areaAddressService.getProvinceNo(tenant, new ProvinceParam(province));
                if (!remoteResult5.isSuccess() || remoteResult5.getT()==null){
                    //获取省份编号失败
                    remoteResult.setResultCode(InvoiceResultCode.GETPROVINCENOFAIL);
                    remoteResult.setResultMsg("获取省份编号失败");
                    LOGGER.info("exchangeToVat返回值==获取省份编号失败==" + JacksonUtil.toJson(remoteResult5));
                    return remoteResult;
                }
                String provinceNo = remoteResult5.getT();

                //添加成功，添加收票地址
                ConsigneeAddressParam param1 = new ConsigneeAddressParam();
                param1.setLenovoId(invoiceChangeApi.getLenovoId());
                param1.setName(name);
                param1.setMobile(phone2);
                param1.setAddress(address2);
                param1.setProvinceCode(province);
                param1.setCityCode(city);
                param1.setCountyCode(county);
                param1.setType("SP");//地址类型为收票地址
                param1.setZip(zip);
                param1.setIsdefault(0);//不设置为默认地址

                LOGGER.info("添加收票地址参数=="+JacksonUtil.toJson(param1));
                RemoteResult<String> remoteResult3 = consigneeAddressService.saveConsignee(tenant, param1);
                if (!remoteResult3.isSuccess()) {
                    //添加收票地址失败
                    remoteResult.setResultCode(InvoiceResultCode.ADDSPADDRESSFAIL);
                    remoteResult.setResultMsg("添加收票地址失败，"+remoteResult3.getResultMsg());
                    LOGGER.info("exchangeToVat返回值==添加收票地址失败==" + JacksonUtil.toJson(remoteResult3));
                    return remoteResult;
                }

                //添加成功，再次判断订单状态
                LOGGER.info("获取订单信息==参数==" + orderCode);
                RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId2 = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
                LOGGER.info("获取订单信息==返回值==" + JacksonUtil.toJson(invoiceChangeApiByOrderId2));
                InvoiceChangeApi invoiceChangeApi2 = invoiceChangeApiByOrderId2.getT();
                if (invoiceChangeApi2==null){
                    //获取订单信息失败
                    remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                    remoteResult.setResultMsg("获取订单信息失败");
                    LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //0 已支付，未抛单，1 已抛单、未发货，2已发货
                int orderStatus2 = invoiceChangeApi2.getOrderStatus();
                String applyId = UUID.randomUUID().toString().replace("-", "");
                if (orderStatus2==2) {
                    //订单已发货，不能进行换票操作
                    remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                    remoteResult.setResultMsg("该订单已发货，不能进行换票操作");
                    LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                } else if (orderStatus2==0) {
                    //订单未抛单-已支付，如果是增换增，只修改zid，如果是普换增和电换增，需要新加一条映射，只增加zid和orderCode
                    VathrowBtcp vathrowBtcp = new VathrowBtcp();
                    if (changeType==5){
                        //增换增，只修改zid
                        vathrowBtcp = vathrowBtcpMapper.getVatInvoiceByOrderCode(orderCode);
                        if (vathrowBtcp.getZid()==null){
                            remoteResult.setResultMsg("查询不到该订单信息！");
                            remoteResult.setResultCode(InvoiceResultCode.GETORDERSTATUSFAIL);
                            LOGGER.info("exchangeToVat返回值==" + JacksonUtil.toJson(remoteResult));
                            return remoteResult;
                        }
                        VathrowBtcp vathrowBtcp1 = new VathrowBtcp();
                        vathrowBtcp1.setZid(remoteResult1.getT().getVatInvoiceId() + "");
                        vathrowBtcp1.setOrderCode(orderCode);
                        int j = vathrowBtcpMapper.updateVatBTCP(vathrowBtcp1);
                        if (j<=0){
                            //修改失败
                            remoteResult.setResultCode(InvoiceResultCode.UPDATEVATINVOICEFAIL);
                            remoteResult.setResultMsg("修改增票信息失败！");
                            LOGGER.info("exchangeToVat返回值=="+j+"=="+ JacksonUtil.toJson(remoteResult));
                            return remoteResult;
                        }else {
                            LOGGER.info("增换增，只修改zid成功=="+j+"=="+orderCode);
                        }
                    }else {
                        //普换增和电换增，需要新加一条映射，只增加zid和orderCode TODO 新加之前判断是否只有一条有效记录
                        long l = vatInvoiceService.initVathrowBtcp(orderCode, remoteResult1.getT().getVatInvoiceId() + "", invoiceChangeApi2.getShopId());
                        if (l<=0){
                            //添加失败
                            remoteResult.setResultCode(InvoiceResultCode.UPDATEVATINVOICEFAIL);
                            remoteResult.setResultMsg("添加新得增票和订单映射失败！");
                            LOGGER.info("exchangeToVat返回值=="+l+"=="+ JacksonUtil.toJson(remoteResult));
                            return remoteResult;
                        }else {
                            LOGGER.info("普换增和电换增，新加一条映射成功=="+l+"=="+orderCode);
                        }
                    }
                    //修改增票成功，再修改订单
                    InvoiceChangeApi invoiceChangeApi1 = new InvoiceChangeApi();
                    invoiceChangeApi1.setOrderId(Long.parseLong(orderCode));
                    invoiceChangeApi1.setOrderStatus(0);
                    invoiceChangeApi1.setTitle(newInvoiceTitle);
                    invoiceChangeApi1.setTaxpayerIdentity(newTaxNo);
                    invoiceChangeApi1.setDepositBank(newBankName);
                    invoiceChangeApi1.setBankNo(newBankNo);
                    invoiceChangeApi1.setRegisterAddress(newAddress);
                    invoiceChangeApi1.setRegisterPhone(newPhone);
                    invoiceChangeApi1.setType(vatInvoiceType);
                    invoiceChangeApi1.setUnits(1);
                    invoiceChangeApi1.setName(name);
                    invoiceChangeApi1.setProvince(province);
                    invoiceChangeApi1.setProvinceId(provinceNo);
                    invoiceChangeApi1.setCity(city);
                    invoiceChangeApi1.setCounty(county);
                    invoiceChangeApi1.setAddress(address2);
                    invoiceChangeApi1.setMobile(phone2);
                    invoiceChangeApi1.setZip(zip);
                    invoiceChangeApi1.setShopId(invoiceChangeApi2.getShopId());
                    invoiceChangeApi1.setChangeType(changeType);
                    invoiceChangeApi1.setOperator(itCode);
                    invoiceChangeApi1.setIsNeedMerge(0);
                    invoiceChangeApi1.setZid(remoteResult1.getT().getVatInvoiceId()+"");

                    LOGGER.info("修改订单==参数==" + JacksonUtil.toJson(invoiceChangeApi1));
                    RemoteResult remoteResult2 = vatApiOrderCenter.updateInvoice(invoiceChangeApi1);
                    LOGGER.info("修改订单==返回值==" + JacksonUtil.toJson(remoteResult2));
                    if (remoteResult2.isSuccess()) {
                        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                        remoteResult.setResultMsg("换票成功！");
                        remoteResult.setSuccess(true);

                        ExchangeInvoiceRecord record = new ExchangeInvoiceRecord();
                        try {
                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi2.getOutId());
                            record.setShopid(invoiceChangeApi2.getShopId());
                            record.setState(2);//换票成功，2
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);
                            record.setExchangeType(changeType);
                            record.setLenovoId(invoiceChangeApi2.getLenovoId());
                            //老发票信息
                            record.setOldType(invoiceChangeApi2.getUnits());
                            record.setOldInvoiceId(1);//没有，初始化为1
                            record.setOldInvoiceTitle(invoiceChangeApi2.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi2.getType());
                            record.setOldTaxNo(invoiceChangeApi2.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi2.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi2.getBankNo());
                            record.setOldAddress(invoiceChangeApi2.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi2.getRegisterPhone());
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
                            //收票地址信息
                            record.setName(name);
                            record.setProvince(province);
                            record.setProvinceId(provinceNo);
                            record.setCity(city);
                            record.setCounty(county);
                            record.setAddress(address2);
                            record.setPhone(phone2);
                            record.setZip(zip);

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i <= 0) {
                                ERRORLOGGER.error("添加换票记录失败==参数=="+i+"=="+ JacksonUtil.toJson(record));
                            }else {
                                LOGGER.info("添加换票记录成功!=="+i);
                            }
                        } catch (Exception e) {
                            ERRORLOGGER.error("添加换票记录出现异常==参数==" + JacksonUtil.toJson(record)+"==" + e.getMessage(), e);
                        }
                    } else {
                        //修改订单失败，修改增票要回滚
                        if (changeType==5){
                            //增换增，将修改的zid回滚
                            VathrowBtcp vathrowBtcp1 = new VathrowBtcp();
                            vathrowBtcp1.setZid(vathrowBtcp.getZid());
                            vathrowBtcp1.setOrderCode(orderCode);
                            int j = vathrowBtcpMapper.updateVatBTCP(vathrowBtcp1);
                            if (j<=0){
                                //修改增票回滚失败
                                ERRORLOGGER.info("增换增，修改增票回滚失败==参数=="+j+"=="+JacksonUtil.toJson(vathrowBtcp1));
                            }else {
                                LOGGER.info("增换增，将修改的zid回滚成功！=="+j+"=="+JacksonUtil.toJson(vathrowBtcp1));
                            }
                        }else {
                            //普换增和电换增，需要删除新加的映射  TODO 改为伪删除
                            int i = vathrowBtcpMapper.deleteByOrderCode(orderCode);
                            if (i<=0){
                                //修改增票回滚失败
                                ERRORLOGGER.info("普换增，电换增，修改增票回滚失败==参数=="+i+"=="+orderCode);
                            }else {
                                LOGGER.info("普换增和电换增，删除新加的映射成功！=="+i+"=="+orderCode);
                            }
                        }
                        remoteResult.setResultCode(InvoiceResultCode.UPDATEORDERFAIL);
                        remoteResult.setResultMsg("换票失败，修改订单失败");
                    }
                } else if (orderStatus2==1) {
                    //订单已抛单-未发货，修改BTCP
                    String xml = "<InvoiceEditUnit>";
                    xml += "<ApplyId>" + applyId + "</ApplyId>"; //唯一标识，
                    xml += "<CID>GM</CID>"; //官网是GM
                    xml += "<BTCPSO>" + invoiceChangeApi2.getOutId() + "</BTCPSO>";
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

                    LOGGER.info("调用BTCP参数==" + JacksonUtil.toJson(paramMap));
                    String resposeData = net.requestData(propertiesUtil.getExchangeinvoiceurl(), paramMap);
                    LOGGER.info("调用BTCP返回值==" + resposeData);
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
                            //添加换票记录
                            record.setId(applyId);
                            record.setItCode(itCode);
                            record.setOrderCode(orderCode);
                            record.setBTCPOrderCode(invoiceChangeApi2.getOutId());
                            record.setShopid(invoiceChangeApi2.getShopId());
                            record.setState(1);//换票中，1
                            record.setExchangeTime(date);
                            record.setUpdateTime(date);
                            record.setExchangeType(changeType);
                            record.setLenovoId(invoiceChangeApi2.getLenovoId());
                            //老发票信息
                            record.setOldType(invoiceChangeApi2.getUnits());
                            record.setOldInvoiceId(1);//没有，初始化为1
                            record.setOldInvoiceTitle(invoiceChangeApi2.getTitle());
                            record.setOldInvoiceType(invoiceChangeApi2.getType());
                            record.setOldTaxNo(invoiceChangeApi2.getTaxpayerIdentity());
                            record.setOldBankName(invoiceChangeApi2.getDepositBank());
                            record.setOldBankNo(invoiceChangeApi2.getBankNo());
                            record.setOldAddress(invoiceChangeApi2.getRegisterAddress());
                            record.setOldPhone(invoiceChangeApi2.getRegisterPhone());
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
                            //收票地址信息
                            record.setName(name);
                            record.setProvince(province);
                            record.setProvinceId(provinceNo);
                            record.setCity(city);
                            record.setCounty(county);
                            record.setAddress(address2);
                            record.setPhone(phone2);
                            record.setZip(zip);

                            int i = exchangeInvoiceRecordMapper.addExchangeInvoiceRecord(record);
                            if (i <= 0) {
                                ERRORLOGGER.error("添加换票记录失败==参数=="+i+"=="+ JacksonUtil.toJson(record));
                            }else {
                                LOGGER.info("添加换票记录成功！=="+i);
                            }
                        } catch (Exception e) {
                            ERRORLOGGER.error("添加换票记录出现异常==参数==" + JacksonUtil.toJson(record)+"==" + e.getMessage(), e);
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
    @Override
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

    //导出换票记录，必须指定时间段
    @Override
    public RemoteResult<List<ExchangeInvoiceRecord>> getExchangeInvoiceRecordList(ExchangeInvoiceRecord exchangeInvoiceRecord){
        LOGGER.info("getExchangeInvoiceRecordList参数=="+JacksonUtil.toJson(exchangeInvoiceRecord));

        RemoteResult<List<ExchangeInvoiceRecord>> remoteResult = new RemoteResult<List<ExchangeInvoiceRecord>>();

        try {
            List<ExchangeInvoiceRecord> list = exchangeInvoiceRecordMapper.getExchangeInvoiceRecordList(exchangeInvoiceRecord);

            remoteResult.setSuccess(true);
            remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
            remoteResult.setResultMsg("查询成功");
            remoteResult.setT(list);
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("getExchangeInvoiceRecordList返回值=="+ JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //获取换票记录详情
    @Override
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

            if(exchangeInvoiceRecord==null){
                remoteResult.setResultCode(InvoiceResultCode.GETRECORDFAIL);
                remoteResult.setResultMsg("没有查到换票记录！");
                LOGGER.info("getExchangeInvoiceRecord返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

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

            //获取订单信息
            LOGGER.info("获取订单信息==参数=="+orderCode);
            RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
            LOGGER.info("获取订单信息==返回值=="+JacksonUtil.toJson(invoiceChangeApiByOrderId));
            InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
            if (invoiceChangeApi==null){
                //获取订单信息失败
                remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                remoteResult.setResultMsg("获取订单信息失败");
                LOGGER.info("exchangeToCommon返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            //0 已支付，未抛单，1 已抛单、未发货，2已发货
            int orderStatus = invoiceChangeApi.getOrderStatus();
            if (orderStatus==2){
                remoteResult.setResultMsg("该订单已发货，不允许换票！");
                remoteResult.setResultCode(InvoiceResultCode.UNEXCHANGEINVOICE);
                LOGGER.info("ifExchangeVatInvoice返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            //过滤faType为0或者3，并且shopid为Lenovo或者epp，如果不满足，不允许换票
            Integer faType = invoiceChangeApi.getFaType();
            Integer shopId = invoiceChangeApi.getShopId();
            if ((faType==0 || faType==3) && (shopId==1 || shopId==3)){
                remoteResult.setSuccess(true);
                remoteResult.setResultMsg("可以换票！");
                remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                return remoteResult;
            }
            remoteResult.setResultMsg("该订单FA不符合换票条件");
            remoteResult.setResultCode(InvoiceResultCode.FAIDNOTALLOWEXCHANGE);
            LOGGER.info("ifExchangeVatInvoice返回值==" + JacksonUtil.toJson(remoteResult));
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
                //获取换票记录详情
                record = exchangeInvoiceRecordMapper.getExchangeInvoiceRecord(applyId);
                LOGGER.info("换票记录"+JacksonUtil.toJson(record));
                if(record==null){
                    ERRORLOGGER.error("没有查到换票记录！"+applyId);
                    remoteResult.setResultMsg("接收成功");
                    remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
                    remoteResult.setSuccess(true);
                    LOGGER.info("BTCPCallback返回值==" + JacksonUtil.toJson(remoteResult));
                    return remoteResult;
                }
                //换票成功，修改换票记录状态为换票成功
                try {
                    record.setState(2);
                    record.setUpdateTime(date);
                    int i = exchangeInvoiceRecordMapper.updateExchangeInvoiceRecord(record);
                    if (i<=0){
                        ERRORLOGGER.error("换票成功，换票记录状态修改失败！=="+i+"=="+ JacksonUtil.toJson(record));
                    }else {
                        LOGGER.info("换票成功，换票记录状态修改成功！=="+i+"=="+applyId);
                    }
                }catch (Exception e){
                    ERRORLOGGER.error("换票成功，换票记录状态修改出现异常！"+JacksonUtil.toJson(record),e);
                }
                //换票类型，1电换普，2增换普、3普换普，4电换增、5增换增、6普换增
                Integer exchangeType = record.getExchangeType();
                Tenant tenant = new Tenant();
                tenant.setShopId(record.getShopid());
                try {
                    //修改成功，调用订单修改接口
                    InvoiceChangeApi invoiceChangeApi = new InvoiceChangeApi();
                    invoiceChangeApi.setOrderId(Long.parseLong(record.getOrderCode()));
                    invoiceChangeApi.setOrderStatus(1);
                    invoiceChangeApi.setTitle(record.getNewInvoiceTitle());
                    invoiceChangeApi.setTaxpayerIdentity(record.getNewTaxNo());
                    invoiceChangeApi.setDepositBank(record.getNewBankName());
                    invoiceChangeApi.setBankNo(record.getNewBankNo());
                    invoiceChangeApi.setRegisterAddress(record.getNewAddress());
                    invoiceChangeApi.setRegisterPhone(record.getNewPhone());
                    invoiceChangeApi.setType(record.getNewInvoiceType());
                    invoiceChangeApi.setUnits(record.getNewType());
                    invoiceChangeApi.setName(record.getName());
                    invoiceChangeApi.setProvince(record.getProvince());
                    invoiceChangeApi.setProvinceId(record.getProvinceId());
                    invoiceChangeApi.setCity(record.getCity());
                    invoiceChangeApi.setCounty(record.getCounty());
                    invoiceChangeApi.setAddress(record.getAddress());
                    invoiceChangeApi.setMobile(record.getPhone());
                    invoiceChangeApi.setZip(record.getZip());
                    invoiceChangeApi.setChangeType(record.getExchangeType());
                    invoiceChangeApi.setOperator(record.getItCode());
                    invoiceChangeApi.setShopId(record.getShopid());
                    invoiceChangeApi.setIsNeedMerge(0);
                    if (exchangeType==4 || exchangeType==5 || exchangeType==6){
                        invoiceChangeApi.setZid(record.getNewInvoiceId()+"");
                    }

                    LOGGER.info("BTCP通知=修改订单参数==" + JacksonUtil.toJson(invoiceChangeApi));
                    RemoteResult remoteResult1 = vatApiOrderCenter.updateInvoice(invoiceChangeApi);
                    LOGGER.info("BTCP通知=修改订单返回值=="+JacksonUtil.toJson(remoteResult1));
                    if (!remoteResult1.isSuccess()){
                        ERRORLOGGER.error("BTCP通知=修改订单失败！applyId=" + applyId + "==" + JacksonUtil.toJson(invoiceChangeApi));
                    }
                }catch (Exception e){
                    ERRORLOGGER.error("BTCP通知==修改订单出现异常！==" + applyId+"==" + e.getMessage(), e);
                }
                try {
                    //修改增票
                    VathrowBtcp vathrowBtcp = new VathrowBtcp();
                    vathrowBtcp.setOrderCode(record.getOrderCode());
                    //如果是增换普，将增票和订单的映射记录删除  TODO 改为伪删除
                    if (exchangeType==2){
                        VathrowBtcp vathrowBtcp1 = vathrowBtcpMapper.getVatInvoiceByOrderCode(record.getOrderCode());
                        int i = vathrowBtcpMapper.deleteByOrderCode(record.getOrderCode());
                        if (i<=0){
                            ERRORLOGGER.error("增换普，换票成功，增票和订单映射删除失败！=="+i+"=="+record.getOrderCode());
                        }else {
                            LOGGER.info("增换普，换票成功，增票和订单映射删除成功！=="+i+"=="+JacksonUtil.toJson(vathrowBtcp1));
                        }
                    } else if (exchangeType==5){
                        //如果是增换增，修改原来的映射记录
                        vathrowBtcp.setZid(record.getNewInvoiceId()+"");
                        vathrowBtcp.setTitle(record.getNewInvoiceTitle());
                        vathrowBtcp.setTaxpayeridentity(record.getNewTaxNo());
                        vathrowBtcp.setDepositbank(record.getNewBankName());
                        vathrowBtcp.setBankno(record.getNewBankNo());
                        vathrowBtcp.setRegisteraddress(record.getNewAddress());
                        vathrowBtcp.setRegisterphone(record.getNewPhone());
                        vathrowBtcp.setName(record.getName());
                        vathrowBtcp.setProvinceid(record.getProvinceId());
                        vathrowBtcp.setCity(record.getCity());
                        vathrowBtcp.setCounty(record.getCounty());
                        vathrowBtcp.setAddress(record.getAddress());
                        vathrowBtcp.setPhone(record.getPhone());
                        vathrowBtcp.setZip(record.getZip());
                        int i = vathrowBtcpMapper.updateVatBTCP(vathrowBtcp);
                        if (i<=0){
                            ERRORLOGGER.error("BTCP回调==增换增==修改增票信息失败"+record.getOrderCode()+"=="+applyId);
                        }else {
                            LOGGER.info("BTCP回调==增换增==修改增票信息成功！=="+i+"=="+record.getOrderCode()+"=="+applyId);
                        }
                    }else if (exchangeType==4 || exchangeType==6){
                        //如果是普换增，电换增，需要增加一条新的映射  TODO 修改之前判断是否只有一条有效记录
                        vathrowBtcp.setTitle(record.getNewInvoiceTitle());
                        vathrowBtcp.setTaxpayeridentity(record.getNewTaxNo());
                        vathrowBtcp.setDepositbank(record.getNewBankName());
                        vathrowBtcp.setBankno(record.getNewBankNo());
                        vathrowBtcp.setRegisteraddress(record.getNewAddress());
                        vathrowBtcp.setRegisterphone(record.getNewPhone());
                        vathrowBtcp.setOutid(record.getBTCPOrderCode());
                        vathrowBtcp.setMembercode(record.getLenovoId());
                        vathrowBtcp.setName(record.getName());
                        vathrowBtcp.setProvinceid(record.getProvinceId());
                        vathrowBtcp.setCity(record.getCity());
                        vathrowBtcp.setCounty(record.getCounty());
                        vathrowBtcp.setAddress(record.getAddress());
                        vathrowBtcp.setPhone(record.getPhone());
                        vathrowBtcp.setZip(record.getZip());
                        vathrowBtcp.setIsneedmerge(0);
                        vathrowBtcp.setThrowingStatus(0);
                        vathrowBtcp.setZid(record.getNewInvoiceId() + "");
                        vathrowBtcp.setOrderStatus(2);

                        LOGGER.info("添加新映射"+JacksonUtil.toJson(vathrowBtcp));
                        int i = vathrowBtcpMapper.addVathrowBtcp(vathrowBtcp);
                        if (i<=0){
                            ERRORLOGGER.error("BTCP回调==普换增，电换增==新加映射失败=="+i+"=="+applyId+"=="+JacksonUtil.toJson(vathrowBtcp));
                        }else {
                            LOGGER.info("BTCP回调==普换增，电换增==新加映射成功=="+i+"=="+applyId+"=="+record.getOrderCode());
                        }
                    }
                }catch (Exception e){
                    ERRORLOGGER.error("BTCP回调===修改增票信息出现异常==" + record.getOrderCode()+"=="+applyId+"=="+e.getMessage(),e);
                }
            }else {
                //换票失败
                try {
                    record.setId(applyId);
                    record.setState(3);
                    record.setUpdateTime(date);
                    int i = exchangeInvoiceRecordMapper.updateExchangeInvoiceRecord(record);
                    if (i<=0){
                        ERRORLOGGER.error("换票失败，换票记录状态修改失败！=="+i+"==" + JacksonUtil.toJson(record));
                    }else {
                        LOGGER.info("换票失败，换票记录状态修改成功！=="+i);
                    }
                }catch (Exception e){
                    ERRORLOGGER.error("换票失败，换票记录状态修改出现异常！"+JacksonUtil.toJson(record),e);
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        remoteResult.setResultMsg("接收成功");
        remoteResult.setResultCode(InvoiceResultCode.SUCCESS);
        remoteResult.setSuccess(true);
        LOGGER.info("BTCPCallback返回值=="+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    //换增票校验接口，如果存在，回显增票信息
    @Override
    public RemoteResult<GetVatInvoiceInfoResult> ifVatInvoiceExist(String taxNO,String orderCode) {
        LOGGER.info("ifVatInvoiceExist参数==orderCode="+orderCode+";taxNO="+taxNO);
        RemoteResult<GetVatInvoiceInfoResult> remoteResult = new RemoteResult<GetVatInvoiceInfoResult>();
        try {
            if (StringUtil.isEmpty(taxNO,orderCode)){
                remoteResult.setResultCode(InvoiceResultCode.PARAMSFAIL);
                remoteResult.setResultMsg("必填参数错误");
                LOGGER.info("ifVatInvoiceExist返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }

            //获取订单信息
            LOGGER.info("获取订单信息==参数=="+orderCode);
            RemoteResult<InvoiceChangeApi> invoiceChangeApiByOrderId = vatApiOrderCenter.getInvoiceChangeApiByOrderId(orderCode);
            LOGGER.info("获取订单信息==返回值=="+JacksonUtil.toJson(invoiceChangeApiByOrderId));
            InvoiceChangeApi invoiceChangeApi = invoiceChangeApiByOrderId.getT();
            if (invoiceChangeApi==null){
                //获取订单信息失败
                remoteResult.setResultCode(InvoiceResultCode.GETORDERFAIL);
                remoteResult.setResultMsg("获取订单信息失败");
                LOGGER.info("ifVatInvoiceExist返回值==" + JacksonUtil.toJson(remoteResult));
                return remoteResult;
            }
            GetVatInvoiceInfoParam param = new GetVatInvoiceInfoParam();
            param.setLenovoId(invoiceChangeApi.getLenovoId());
            param.setShopId(invoiceChangeApi.getShopId());
            param.setFaid(invoiceChangeApi.getFaid());
            param.setFaType(invoiceChangeApi.getFaType() + "");
            param.setTaxNo(taxNO);
            remoteResult = invoiceApiService.getVatInvoiceInfo(param, new Tenant());
        }catch (Exception e){
            remoteResult.setResultCode(InvoiceResultCode.FAIL);
            remoteResult.setResultMsg("系统异常");
            LOGGER.error(e.getMessage(),e);
        }
        LOGGER.info("ifExchangeInvoice End返回值 : "+JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }
}
