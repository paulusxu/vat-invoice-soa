package com.lenovo.invoice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.invoice.api.InvoiceShopApiService;
import com.lenovo.invoice.common.utils.*;
import com.lenovo.invoice.dao.InvoiceShopMapper;
import com.lenovo.invoice.domain.InvoiceIdAndUuid;
import com.lenovo.invoice.domain.InvoiceShop;
import com.lenovo.invoice.domain.InvoiceShopModifyLog;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.tool.util.StringUtils;
import com.lenovo.open.gateway.java.sdk.JavaSDKClient;
import com.lenovo.open.gateway.java.sdk.util.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by xuweihua on 2016/7/26.
 */
@Service("invoiceShopApiService")
public class InvoiceShopApiServiceImpl implements InvoiceShopApiService {
    private static final Logger logger = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.17shop");
    @Autowired
    private InvoiceShopMapper invoiceShopMapper;

    @Autowired
    private PropertiesConfig propertiesConfig;

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
            if(StringUtils.isEmpty(invoiceShop.getTaxNoType())&&StringUtils.isNotEmpty(invoiceShop.getTaxNo())) {
                if (invoiceShop.getTaxNo().length() == 15 || invoiceShop.getTaxNo().length() == 20) {
                    invoiceShop.setTaxNoType("0");
                } else if (invoiceShop.getTaxNo().length() == 18) {
                    invoiceShop.setTaxNoType("1");
                }
            }
            if (invoiceShop.getSynType() == 1) {
                if (StringUtils.isEmpty(invoiceShop.getPayMan())) {
                    invoiceShop.setPayMan(invoiceShop.getCustomerName());
                }
                if (invoiceShop.getPayManType() == null) {
                    invoiceShop.setPayManType(invoiceShop.getCompanyType());
                }
            }

            if (invoiceShop.getIsDefault() == null) {
                invoiceShop.setIsDefault(0);
            }
            if (invoiceShop.getSynType() == 1) {
                //判断customername,taxno 是否有单独存在的
                if(invoiceShop.getCompanyType()==0&&"个人".equals(invoiceShop.getCustomerName())){
                    invoiceShop.setApprovalStatus(2);
                }

                String ret=ShopHttpClientUtil.sendPost(propertiesConfig.getSmbUrl()+"/invoices", getInvoiceJson(invoiceShop).toString());
                JSONObject retJson=JSONObject.parseObject(ret);
                if("1".equals(retJson.getString("code"))){
                    invoiceIdAndUuid.setUuid(retJson.getJSONObject("data").getString("id"));
                    remoteResult.setT(invoiceIdAndUuid);
                }else {
                    remoteResult.setResultCode(retJson.getString("code"));
                    remoteResult.setResultMsg(retJson.getString("msg"));
                    return remoteResult;
                }
            } else if (invoiceShop.getSynType() == 2) {
                String ret=ShopHttpClientUtil.sendPut(propertiesConfig.getSmbUrl() + "/invoices", getInvoiceJson(invoiceShop).toString());
                JSONObject retJson=JSONObject.parseObject(ret);
                if("1".equals(retJson.getString("code"))){
                    remoteResult.setSuccess(true);
                }else {
                    remoteResult.setResultCode(retJson.getString("code"));
                    remoteResult.setResultMsg(retJson.getString("msg"));
                    return remoteResult;
                }
            } else if (invoiceShop.getSynType() == 3) {
                JSONObject invoicesJson=ShopHttpClientUtil.sendGet(propertiesConfig.getSmbUrl() + "/invoices/" + invoiceShop.getUuid());
                if("1".equals(invoicesJson.getString("code"))) {
                    JSONObject jsonObject = invoicesJson.getJSONObject("data");
                    jsonObject.put("datafrom", "C_BTC");
                    jsonObject.put("datadependon", "C_BTC");
                    String ret=ShopHttpClientUtil.sendDelete(propertiesConfig.getSmbUrl(), jsonObject.toString());
                    JSONObject retJson=JSONObject.parseObject(ret);
                    if("1".equals(retJson.getString("code"))){
                        remoteResult.setSuccess(true);
                    }else {
                        remoteResult.setResultCode(retJson.getString("code"));
                        remoteResult.setResultMsg(retJson.getString("msg"));
                        return remoteResult;
                    }
                }else {
                    remoteResult.setResultCode(invoicesJson.getString("code"));
                    remoteResult.setResultMsg(invoicesJson.getString("msg"));
                    return remoteResult;
                }

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
                List<InvoiceShop> invoiceShopList = new ArrayList<InvoiceShop>();
                JSONObject retJson=ShopHttpClientUtil.sendGet(propertiesConfig.getSmbUrl() + "/memberinvoice/"+getMemberinfoid(lenovoid)+"/1");
                if("1".equals(retJson.getString("code"))){
                    JSONArray jsonArray=retJson.getJSONObject("data").getJSONArray("content");
                    Iterator<Object> it = jsonArray.iterator();
                    while (it.hasNext()) {
                        JSONObject ob = (JSONObject) it.next();
                        InvoiceShop invoiceShop =getInvoice(ob);
                        invoiceShopList.add(invoiceShop);
                    }
                }
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
                JSONObject retJson=ShopHttpClientUtil.sendGet(propertiesConfig.getSmbUrl() + "/invoices/"+id);
                InvoiceShop invoiceShop=new InvoiceShop();
                if("1".equals(retJson.getString("code"))) {
                    JSONObject jsonObject = retJson.getJSONObject("data");
                    invoiceShop = getInvoice(jsonObject);
                }
                remoteResult.setSuccess(true);
                remoteResult.setT(invoiceShop);
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
        remoteResult=synInvoice(invoiceShop);
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
                JSONObject retJson=ShopHttpClientUtil.sendGet(propertiesConfig.getSmbUrl() + "/invoices/"+id);
                InvoiceShop invoice=new InvoiceShop();
                if("1".equals(retJson.getString("code"))) {
                    JSONObject jsonObject = retJson.getJSONObject("data");
                    invoice = getInvoice(jsonObject);
                }
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
            logger.error(e.getMessage(), e);
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

    private JSONObject getInvoiceJson(InvoiceShop invoiceShop){
        JSONObject invoiceJson=new JSONObject();
        if(StringUtil.isNotEmpty(invoiceShop.getUuid())){
            invoiceJson.put("id", invoiceShop.getUuid());
        }
        if(StringUtil.isNotEmpty(invoiceShop.getFileURL())){
            invoiceJson.put("isneedacc", 1);
            invoiceJson.put("fileurl", invoiceShop.getFileURL());
        }else {
            invoiceJson.put("isneedacc", 0);
        }
        invoiceJson.put("invoicename", invoiceShop.getCustomerName());
        invoiceJson.put("memberinfoid",getMemberinfoid(invoiceShop.getLenovoID()));
        invoiceJson.put("companytype", invoiceShop.getCompanyType());
        invoiceJson.put("invoicesort", "");
        invoiceJson.put("taxnotype", invoiceShop.getTaxNoType());
        invoiceJson.put("invoicetype", invoiceShop.getInvoiceType());
        invoiceJson.put("paymantype", invoiceShop.getPayManType());
        invoiceJson.put("payman", invoiceShop.getPayMan());
        invoiceJson.put("taxid", invoiceShop.getTaxNo());
        invoiceJson.put("bankname", invoiceShop.getBankName());
        invoiceJson.put("bankid", invoiceShop.getAccountNo());
        invoiceJson.put("zip", invoiceShop.getZip());
        invoiceJson.put("provincecode", invoiceShop.getProvinceCode());
        invoiceJson.put("provincename", invoiceShop.getProvinceName());
        invoiceJson.put("citycode", invoiceShop.getCityCode());
        invoiceJson.put("cityname", invoiceShop.getCityName());
        invoiceJson.put("countycode", invoiceShop.getCountyCode());
        invoiceJson.put("countyname", invoiceShop.getCountyName());
        invoiceJson.put("address", invoiceShop.getAddress());
        invoiceJson.put("phone", invoiceShop.getPhoneNo());
        invoiceJson.put("isshow", invoiceShop.getIsShow());
        invoiceJson.put("isdefault", invoiceShop.getIsDefault());
        invoiceJson.put("processstatus", "");
        invoiceJson.put("operationtype", "");
        invoiceJson.put("approvalstatus", invoiceShop.getApprovalStatus());
        invoiceJson.put("soldtocode", invoiceShop.getSoldToCode());
        invoiceJson.put("datafrom", "C_BTC");
        invoiceJson.put("datadependon", "C_BTC");
        return invoiceJson;
    }

    private InvoiceShop getInvoice(JSONObject jsonObject) {
        InvoiceShop invoiceShop=new InvoiceShop();
        invoiceShop.setUuid(jsonObject.getString("id"));
        invoiceShop.setCustomerName(jsonObject.getString("invoicename"));
        invoiceShop.setCompanyType(jsonObject.getInteger("companytype"));
        invoiceShop.setTaxNoType(jsonObject.getString("taxnotype"));
        invoiceShop.setInvoiceType(jsonObject.getInteger("invoicetype"));
        invoiceShop.setPayManType(jsonObject.getInteger("paymantype"));
        invoiceShop.setPayMan(jsonObject.getString("payman"));
        invoiceShop.setTaxNo(jsonObject.getString("taxid"));
        invoiceShop.setBankName(jsonObject.getString("bankname"));
        invoiceShop.setAccountNo(jsonObject.getString("bankid"));
        invoiceShop.setZip(jsonObject.getString("zip"));
        invoiceShop.setProvinceCode(jsonObject.getString("provincecode"));
        invoiceShop.setProvinceName(jsonObject.getString("provincename"));
        invoiceShop.setCityCode(jsonObject.getString("citycode"));
        invoiceShop.setCityName(jsonObject.getString("cityname"));
        invoiceShop.setCountyCode(jsonObject.getString("countycode"));
        invoiceShop.setCountyName(jsonObject.getString("countyname"));
        invoiceShop.setAddress(jsonObject.getString("address"));
        invoiceShop.setPhoneNo(jsonObject.getString("phone"));
        invoiceShop.setIsShow(jsonObject.getInteger("isshow"));
        invoiceShop.setIsDefault(jsonObject.getInteger("isdefault"));
        invoiceShop.setApprovalStatus(jsonObject.getInteger("approvalstatus"));
        invoiceShop.setSoldToCode(jsonObject.getString("soldtocode"));
        return invoiceShop;
    }
    private String getMemberinfoid(String lenovoid){
        Map<String, Object> lenovo_param_json = new HashMap<String, Object>();
        lenovo_param_json.put("customerId", lenovoid);
        lenovo_param_json.put("sts", propertiesConfig.getSts());
        Response response = null;
        try {
            response = JavaSDKClient.proxy(propertiesConfig.getInterfaceUrl(), propertiesConfig.getAppKey(), propertiesConfig.getAppSecret(), propertiesConfig.getMethod(), lenovo_param_json, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("getMemberinfoid>>status============" + response.getStatus());
        logger.info("getMemberinfoid>>body============" + response.getBody().toString());

        JSONObject jsonObject=JSONObject.parseObject(response.getBody().toString());
        JSONObject data=jsonObject.getJSONObject("result").getJSONObject("lenovo_customer_open_getCustomerbyid_response");
        if(data!=null) {
            if("0".equals(data.getString("ret"))){
                return data.getJSONObject("obj").getString("customerguid");
            }
        }
        return "";
    }

    public static void main(String[] args) {
//        InvoiceShop invoiceShop=new InvoiceShop();
        try {
//            invoiceShop.setUuid("37aa14a7-bac8-4341-b255-f39e51621290");
//            invoiceShop.setCustomerName("测试发票公司修改xx");
//            invoiceShop.setLenovoID("154463");
//            invoiceShop.setInvoiceType(1);
//            invoiceShop.setPayManType(1);
//            invoiceShop.setPayMan("测试发票公司");
//            invoiceShop.setTaxNo("273489875989687586");
//            invoiceShop.setTaxNoType("1");
//            invoiceShop.setCompanyType(1);
//            invoiceShop.setBankName("银行");
//            invoiceShop.setAccountNo("3342342323");
//            invoiceShop.setSubAreaName("海淀");
//            invoiceShop.setZip("100000");
//            invoiceShop.setProvinceName("北京");
//            invoiceShop.setProvinceCode("北京");
//            invoiceShop.setCityName("北京");
//            invoiceShop.setCityCode("北京");
//            invoiceShop.setCountyName("XXX县");
//            invoiceShop.setCountyCode("XXX县");
//            invoiceShop.setAddress("MXMXMXM地址");
//            invoiceShop.setPhoneNo("手机号");
//            invoiceShop.setIsDefault(0);
//            invoiceShop.setSynType(1);
//            invoiceShop.setShopId(8);
//            invoiceShop.setFileURL("x");
//
//            String memberinfoid="";
//            Map<String, Object> lenovo_param_json = new HashMap<String, Object>();
//            lenovo_param_json.put("customerId", invoiceShop.getLenovoID());
//            lenovo_param_json.put("sts", "e40e7004-4c8a-4963-8564-31271a8337d8");
//            Response response = null;
//            try {
//                response = JavaSDKClient.proxy("http://open.lenovouat.com/router/json.jhtm", "yg7IRC","W!rwc5tesI2YgrFEdJ7#", "lenovo.customer.open.getCustomerbyid", lenovo_param_json, null, null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            logger.info("getMemberinfoid>>status============" + response.getStatus());
//            logger.info("getMemberinfoid>>body============" + response.getBody().toString());
//
//            JSONObject jsonObject=JSONObject.parseObject(response.getBody().toString());
//            JSONObject data=jsonObject.getJSONObject("result").getJSONObject("lenovo_customer_open_getCustomerbyid_response");
//            if(data!=null) {
//                if("0".equals(data.getString("ret"))){
//                    memberinfoid= data.getJSONObject("obj").getString("customerguid");
//                }
//            }
//
//            JSONObject invoiceJson=new JSONObject();
//            if(StringUtil.isNotEmpty(invoiceShop.getUuid())){
//                invoiceJson.put("id", invoiceShop.getUuid());
//            }
//            if(StringUtil.isNotEmpty(invoiceShop.getFileURL())){
//                invoiceJson.put("isneedacc", 1);
//                invoiceJson.put("fileurl", invoiceShop.getFileURL());
//            }else {
//                invoiceJson.put("isneedacc", 0);
//            }
//            invoiceJson.put("invoicename", invoiceShop.getCustomerName());
//            invoiceJson.put("memberinfoid","00182108-082a-45bc-b8b2-2181800bb72c");
//            invoiceJson.put("companytype", invoiceShop.getCompanyType());
//            invoiceJson.put("invoicesort", "");
//            invoiceJson.put("taxnotype", invoiceShop.getTaxNoType());
//            invoiceJson.put("invoicetype", invoiceShop.getInvoiceType());
//            invoiceJson.put("paymantype", invoiceShop.getPayManType());
//            invoiceJson.put("payman", invoiceShop.getPayMan());
//            invoiceJson.put("taxid", invoiceShop.getTaxNo());
//            invoiceJson.put("bankname", invoiceShop.getBankName());
//            invoiceJson.put("bankid", invoiceShop.getAccountNo());
//            invoiceJson.put("zip", invoiceShop.getZip());
//            invoiceJson.put("provincecode", invoiceShop.getProvinceCode());
//            invoiceJson.put("provincename", invoiceShop.getProvinceName());
//            invoiceJson.put("citycode", invoiceShop.getCityCode());
//            invoiceJson.put("cityname", invoiceShop.getCityName());
//            invoiceJson.put("countycode", invoiceShop.getCountyCode());
//            invoiceJson.put("countyname", invoiceShop.getCountyName());
//            invoiceJson.put("address", invoiceShop.getAddress());
//            invoiceJson.put("phone", invoiceShop.getPhoneNo());
//            invoiceJson.put("isshow", invoiceShop.getIsShow());
//            invoiceJson.put("isdefault", invoiceShop.getIsDefault());
//            invoiceJson.put("processstatus", "");
//            invoiceJson.put("operationtype", "");
//            invoiceJson.put("approvalstatus", invoiceShop.getApprovalStatus());
//            invoiceJson.put("soldtocode", invoiceShop.getSoldToCode());
//            invoiceJson.put("datafrom", "C_BTC");
//            invoiceJson.put("datadependon", "C_BTC");
//
//            String ret=ShopHttpClientUtil.sendPost("http://10.99.206.102:8014/invoices", invoiceJson.toString());
//            JSONObject retJson=JSONObject.parseObject(ret);
//            System.out.println(invoiceJson.toString());
//            JSONObject ret=ShopHttpClientUtil.sendGet("http://10.99.206.102:8014/memberinvoice/00182108-082a-45bc-b8b2-2181800bb72c/1");
            JSONObject ret=ShopHttpClientUtil.sendGet("http://10.99.206.102:8014/invoices/8ed6b48c-3626-4c48-b6b1-06e8bed67ab2");

//            String ret=ShopHttpClientUtil.sendDelete("http://10.99.206.102:8014/invoices", "{\"approvalstatus\":3,\"phone\":\"手机号\",\"bankid\":\"3342342323\",\"memberinfoid\":\"00182108-082a-45bc-b8b2-2181800bb72c\",\"fileurl\":\"x\",\"taxnotype\":1,\"invoicetype\":0,\"createtime\":1505788879000,\"id\":\"8ed6b48c-3626-4c48-b6b1-06e8bed67ab2\",\"citycode\":\"北京\",\"provincename\":\"北京\",\"provincecode\":\"010\",\"countycode\":\"XXX县\",\"paymantype\":1,\"taxid\":\"273489875989687586\",\"zip\":\"100000\",\"companytype\":1,\"isdefault\":0,\"bankname\":\"银行\",\"payman\":\"测试发票公司修改xx\",\"isneedacc\":1,\"subareaname\":\"京津冀分区\",\"invoicesort\":\"01\",\"invoicename\":\"测试发票公司修改xx\",\"subareacode\":\"100101\",\"countyname\":\"XXX县\",\"address\":\"MXMXMXM地址\",\"cityname\":\"北京\",\"datafrom\":\"C_BTC\",\"datadependon\":\"C_BTC\"}");
            System.out.println(ret.toString());



//            List<InvoiceShop> invoiceShopList = new ArrayList<InvoiceShop>();
//            JSONObject retJson=ShopHttpClientUtil.sendGet("http://10.99.206.102:8014/memberinvoice/00182108-082a-45bc-b8b2-2181800bb72c/1");
//            System.out.println(retJson.toString());
//            if("1".equals(retJson.getString("code"))){
//                JSONArray jsonArray=retJson.getJSONObject("data").getJSONArray("content");
//                Iterator<Object> it = jsonArray.iterator();
//                while (it.hasNext()) {
//                    JSONObject ob = (JSONObject) it.next();
//                    InvoiceShop invoiceShop =getInvoice(ob);
//                    invoiceShopList.add(invoiceShop);
//                }
//                System.out.println(invoiceShopList);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
