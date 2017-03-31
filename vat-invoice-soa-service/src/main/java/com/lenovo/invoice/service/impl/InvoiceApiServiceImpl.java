package com.lenovo.invoice.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.base.Strings;
import com.lenovo.invoice.api.InvoiceApiService;
import com.lenovo.invoice.common.CacheConstant;
import com.lenovo.invoice.common.utils.ErrorUtils;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.common.utils.O2oFaIdUtil;
import com.lenovo.invoice.dao.MemberVatInvoiceMapper;
import com.lenovo.invoice.dao.VatInvoiceMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.MemberVatInvoice;
import com.lenovo.invoice.domain.O2oVatInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.AddVatInvoiceInfoParam;
import com.lenovo.invoice.domain.param.GetVatInvoiceInfoListParam;
import com.lenovo.invoice.domain.param.GetVatInvoiceInfoParam;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import com.lenovo.invoice.domain.result.AddVatInvoiceInfoResult;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.invoice.service.MemberVatInvoiceService;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.invoice.service.redisObject.RedisObjectManager;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import com.lenovo.m2.arch.tool.util.StringUtils;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.stock.soa.api.service.StoreInfoApiService;
import com.lenovo.m2.stock.soa.domain.param.GetStoreInfoIdParam;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mayan3 on 2016/6/20.
 */
@Service("invoiceApiService")
public class InvoiceApiServiceImpl extends BaseService implements InvoiceApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceApiServiceImpl.class);
    private static final Logger LOGGER_BTCP = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.throwBtcp");
    private static final Logger LOGGER_UPDATEZID = LoggerFactory.getLogger("com.lenovo.invoice.service.impl.updateZid");

    @Autowired
    private VatInvoiceMapper vatInvoiceMapper;
    @Autowired
    private MemberVatInvoiceMapper memberVatInvoiceMapper;

    @Autowired
    private MemberVatInvoiceService memberVatInvoiceService;

    @Autowired
    private RedisObjectManager redisObjectManager;

    @Autowired
    private StoreInfoApiService storeInfoApiService;
    @Autowired
    private VathrowBtcpMapper vathrowBtcpMapper;
    @Autowired
    private VatInvoiceService vatInvoiceService;
    @Autowired
    private VatApiOrderCenter vatApiOrderCenter;


    @Override
    public String getType(String faid) {
        String type = null;
        String cacheKey = CacheConstant.CACHE_PREFIX_INIT_FAID + faid;
        if (redisObjectManager.existsKey(cacheKey)) {//获取fatype,没有增加缓存
            type = redisObjectManager.getString(cacheKey);
        } else {
            type = getFaType(faid);
            redisObjectManager.setString(cacheKey, type);
        }
        return type;
    }

    @Override
    public List<VathrowBtcp> getThrowBtcpList() {
        List<VathrowBtcp> btcpList = null;
        try {
            btcpList = vatInvoiceService.getThrowBtcpList();
        } catch (Exception e) {
            LOGGER_BTCP.error(e.getMessage(), e);
        }
        return btcpList;
    }

    @Override
    public PageModel2<VatInvoice> getNotThrowBtcpVatInvoicePage(PageQuery pageQuery, Map map) {
        List<VatInvoice> invoiceList = null;
        try {
            int count = vathrowBtcpMapper.getNotThrowBtcpVatInvoiceCount(map);
            pageQuery.setTotalCount(count);
            if (pageQuery.getTotalCount() == 0) {
                PageModel2<VatInvoice> pageModel2 = new PageModel2<VatInvoice>(pageQuery, new ArrayList<VatInvoice>());
                return pageModel2;
            }

            int pageIndex = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
            int pageSize = pageQuery.getPageSize();
            map.put("pageIndex", pageIndex);//0
            map.put("pageSize", pageSize);//10
            invoiceList = vathrowBtcpMapper.getNotThrowBtcpVatInvoicePage(map);
        } catch (Exception e) {
            LOGGER_BTCP.error(e.getMessage(), e);
        }
        return new PageModel2<VatInvoice>(pageQuery, invoiceList);
    }

    @Override
    public void throwBTCP(List<VathrowBtcp> btcpList) {
        try {
            vatInvoiceService.throwBTCP(btcpList);
        } catch (Exception e) {
            LOGGER_BTCP.error(e.getMessage(), e);
        }
    }

    @Override
    public long updateZid(List<Long> listZids, String zid) {
        LOGGER_UPDATEZID.info("updateZid Start:{},{}", JacksonUtil.toJson(listZids), zid);
        long rows = 0;
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listZids.size() - 1; i++) {
                sb.append(listZids.get(i)).append(",");
            }
            sb.append(listZids.get(listZids.size() - 1));

            rows = vatInvoiceService.updateZid(zid, sb.toString());
            LOGGER_UPDATEZID.info("updateZid End:{}", rows);
        } catch (Exception e) {
            LOGGER_UPDATEZID.error(e.getMessage(), e);
        }

        return rows;
    }

    @Override
    public long makeUpVatInvocie(String zids) {
        try {
            LOGGER.info("MakeUpVatInvocie Start:{}", zids);

            List<VatInvoice> vatInvoiceList = new ArrayList<VatInvoice>();
            String[] zidArr = zids.split(",");
            for (String zid : zidArr) {
                VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(Long.parseLong(zid));
                vatInvoiceList.add(vatInvoice);
            }

            List<Invoice> orderList = new ArrayList<Invoice>();
            if (CollectionUtils.isNotEmpty(vatInvoiceList)) {
                for (VatInvoice vatInvoice : vatInvoiceList) {
                    Invoice order = new Invoice();
                    order.setBankNo(vatInvoice.getAccountno());//账号
                    order.setRegisterAddress(vatInvoice.getAddress());//地址
                    order.setDepositBank(vatInvoice.getBankname());//开户行
                    order.setTitle(vatInvoice.getCustomername());//客户名称
                    order.setTaxpayerIdentity(vatInvoice.getTaxno());//税号
                    order.setRegisterPhone(vatInvoice.getPhoneno());
                    order.setZid(String.valueOf(vatInvoice.getId()));
                    orderList.add(order);
                }
            }
            RemoteResult remoteResult = vatApiOrderCenter.invoiceCallBackService(orderList, 0);
            LOGGER.info("MakeUpVatInvocie End:" + JacksonUtil.toJson(remoteResult));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int updateThrowingStatus(String orderCode, int status) {
        int rows=0;
        try {
            rows=vathrowBtcpMapper.updateThrowingStatus(orderCode,status);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return rows;
    }

    @Override
    public long updateVatInvoice(UpdateVatInvoiceBatchParam param) {
        long rows=0;
        try {
            rows=vathrowBtcpMapper.updateVatInvoice(param);

        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return rows;
    }


    @Override
    public RemoteResult<GetVatInvoiceInfoResult> getVatInvoiceInfo(GetVatInvoiceInfoParam param, Tenant tenant) {
        LOGGER.info("GetVatInvoiceInfo Start:" + JacksonUtil.toJson(param));
        RemoteResult<GetVatInvoiceInfoResult> remoteResult = new RemoteResult<GetVatInvoiceInfoResult>(false);
        try {
            String customername = !Strings.isNullOrEmpty(param.getCustomerName()) ? param.getCustomerName().trim() : param.getCustomerName();
            String taxno = param.getTaxNo();
            String lenovoId = param.getLenovoId();


            //判断入参是否为空
            if (StringUtils.isEmpty(lenovoId) || param.getFaid() == null) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }

            //判断lenovoid是否有对应的增票映射
            VatInvoice vatInvoice = null;

            String storeId = null;
            String faid = param.getFaid();
            String type = getType(faid);
//            if(param.getFaid().equals(O2oFaIdUtil.getProperty("o2ofaid"))){
//                GetStoreInfoIdParam storeInfoIdParam = new GetStoreInfoIdParam();
//                storeInfoIdParam.setFaid(param.getFaid());
//                storeInfoIdParam.setRegion(param.getRegion());
//                RemoteResult remoteResultStoreInfo = storeInfoApiService.getStoreInfoId(storeInfoIdParam);
//                if (remoteResultStoreInfo.isSuccess()) {
//                    storeId = (String) remoteResultStoreInfo.getT();
//                }else {
//                    remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
//                    remoteResult.setResultMsg("获取storeID错误");
//                    return remoteResult;
//                }
//            }
            if (type.equals("0")) {
                faid = param.getFaid();
            }

            if (Strings.isNullOrEmpty(customername) && Strings.isNullOrEmpty(taxno)) {
                List<MemberVatInvoice> memberVatInvoiceList = memberVatInvoiceMapper.getMemberVatInvoiceByLenovoId(lenovoId, type, faid, storeId);

                //按lenovoId增票信息
                if (CollectionUtils.isEmpty(memberVatInvoiceList)) {
                    remoteResult.setResultCode(ErrorUtils.ERR_CODE_NOTEXIST_VAT);
                    remoteResult.setResultMsg("未找到映射，用户:" + lenovoId + "未曾保存过" + customername + ":" + taxno + "增票");
                    return remoteResult;
                }
                //获取最新的一条增票信息
                long zid = memberVatInvoiceList.get(0) == null ? 0 : memberVatInvoiceList.get(0).getInvoiceinfoid();
                vatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(zid);
                if (vatInvoice == null) {
                    remoteResult.setResultCode(ErrorUtils.ERR_CODE_NOTEXIST_VAT);
                    remoteResult.setResultMsg("增值税发票信息不存在或未通过审核或未共享");
                    return remoteResult;
                }
            } else {
                //按customername,taxno取增票信息
                List<VatInvoice> vatInvoiceList = vatInvoiceMapper.getVatInvoice(customername, taxno, type);
                if (CollectionUtils.isEmpty(vatInvoiceList)) {
                    //判断是否是自己填写的资质
                    List<MemberVatInvoice> memberVatInvoiceList = memberVatInvoiceMapper.getMemberVatInvoiceByLenovoId(lenovoId, type, faid, storeId);
                    if (CollectionUtils.isEmpty(memberVatInvoiceList)) {
                        remoteResult.setResultCode(ErrorUtils.ERR_CODE_NOTEXIST_VAT);
                        remoteResult.setResultMsg("未找到映射，用户:" + lenovoId + "未曾保存过" + customername + ":" + taxno + "增票");
                        return remoteResult;
                    }
                    LOGGER.error("####:" + JacksonUtil.toJson(memberVatInvoiceList));
                    for (MemberVatInvoice memberVatInvoice1 : memberVatInvoiceList) {
                        long zid = memberVatInvoice1.getInvoiceinfoid();
                        VatInvoice tVatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(zid);

                        if (tVatInvoice != null) {
                            String tCustomerName = tVatInvoice.getCustomername();
                            String tTaxno = tVatInvoice.getTaxno();

                            if (tCustomerName.equals(customername) && tTaxno.equals(taxno)) {
                                vatInvoice = tVatInvoice;
                                break;
                            }
                        }
                    }
                } else {
                    vatInvoice = vatInvoiceList.get(0);
//                    //写映射表
//
//                    memberVatInvoice = memberVatInvoiceService.getMemberVatInvoice(vatInvoice.getId(), lenovoId, shopId);
//                    if (memberVatInvoice == null) {
//                        memberVatInvoiceService.insertMemberVatInvoice(vatInvoice.getId(), lenovoId, shopId);
//                    }
                }
            }
            if (vatInvoice != null) {
                remoteResult.setSuccess(true);
                remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
                remoteResult.setT(parseGetVatInvoiceInfoResult(vatInvoice, lenovoId));
            }

        } catch (Exception e) {
            remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
            remoteResult.setResultMsg("系统异常错误");
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("GetVatInvoiceInfo End:" + JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult<List<GetVatInvoiceInfoResult>> getVatInvoiceInfo(GetVatInvoiceInfoListParam param, Tenant tenant) {
        RemoteResult<List<GetVatInvoiceInfoResult>> remoteResult = new RemoteResult<List<GetVatInvoiceInfoResult>>(false);
        String lenovoId = param.getLenovoId();
        String faid = param.getFaid();
        String type = getType(faid);

        if (type.equals("0")) {
            faid = param.getFaid();
        }
        try {
            List<MemberVatInvoice> memberVatInvoiceList = memberVatInvoiceMapper.getMemberVatInvoiceByLenovoId(lenovoId, type, faid, null);
            if (CollectionUtils.isNotEmpty(memberVatInvoiceList)) {
                List<GetVatInvoiceInfoResult> infoResults = new ArrayList<GetVatInvoiceInfoResult>();
                for (MemberVatInvoice memberVatInvoice : memberVatInvoiceList) {
                    VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(memberVatInvoice.getInvoiceinfoid());
                    infoResults.add(parseGetVatInvoiceInfoResult(vatInvoice, lenovoId));
                }
                remoteResult.setT(infoResults);
                remoteResult.setSuccess(true);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return remoteResult;
    }

    private GetVatInvoiceInfoResult parseGetVatInvoiceInfoResult(VatInvoice vatInvoice, String lenovoId) {
        GetVatInvoiceInfoResult result = new GetVatInvoiceInfoResult();
        result.setId(vatInvoice.getId());
        result.setPhoneNo(vatInvoice.getPhoneno());
        result.setTaxNo(vatInvoice.getTaxno());
        result.setAddress(vatInvoice.getAddress());
        result.setCustomerName(vatInvoice.getCustomername());
        result.setBankName(vatInvoice.getBankname());
        result.setLenovoId(lenovoId);
        result.setAccountNo(vatInvoice.getAccountno());
        result.setIsShared(vatInvoice.getIsshared() == null || vatInvoice.getIsshared() == 0 ? false : true);
        result.setIsCheck(vatInvoice.getIscheck());
        result.setIsNeedMerge(vatInvoice.getIsNeedMerge() == null || vatInvoice.getIsNeedMerge() == 0 ? false : true);
        result.setShopId(vatInvoice.getShopid());
        result.setType(vatInvoice.getType());
        result.setFaid(vatInvoice.getFaid());
        result.setStoresid(vatInvoice.getStoresid());
        return result;
    }

    private AddVatInvoiceInfoResult parseAddVatInvoiceInfoResult(Object obj, String lenovoId) {
        AddVatInvoiceInfoResult result = new AddVatInvoiceInfoResult();
        if (obj instanceof VatInvoice) {
            VatInvoice vatInvoice = (VatInvoice) obj;
            result.setVatInvoiceId(vatInvoice.getId());
            result.setPhoneNo(vatInvoice.getPhoneno());
            result.setTaxNo(vatInvoice.getTaxno());
            result.setAddress(vatInvoice.getAddress());
            result.setCustomerName(vatInvoice.getCustomername());
            result.setBankName(vatInvoice.getBankname());
            result.setLenovoId(lenovoId);
            result.setAccountNo(vatInvoice.getAccountno());
            result.setIsShared(vatInvoice.getIsshared() == null || vatInvoice.getIsshared() == 0 ? false : true);
            result.setIsCheck(vatInvoice.getIscheck());
            result.setIsNeedMerge(vatInvoice.getIsNeedMerge() == null || vatInvoice.getIsNeedMerge() == 0 ? false : true);
            result.setShopId(vatInvoice.getShopid());
            result.setType(vatInvoice.getType());
            result.setFaid(vatInvoice.getFaid());
            result.setStoreId(vatInvoice.getStoresid());
        }
        if (obj instanceof O2oVatInvoice) {
            O2oVatInvoice vatInvoice = (O2oVatInvoice) obj;
            result.setVatInvoiceId(vatInvoice.getId());
            result.setPhoneNo(vatInvoice.getPhoneno());
            result.setTaxNo(vatInvoice.getTaxno());
            result.setAddress(vatInvoice.getAddress());
            result.setCustomerName(vatInvoice.getCustomername());
            result.setBankName(vatInvoice.getBankname());
            result.setLenovoId(lenovoId);
            result.setAccountNo(vatInvoice.getAccountno());
            result.setShopId(vatInvoice.getShopid());
            result.setFaid(vatInvoice.getFaid());
            result.setStoreId(vatInvoice.getStoreId());
        }
        return result;
    }


    @Override
    public RemoteResult addVatInvoiceInfo(AddVatInvoiceInfoParam param, Tenant tenant) {
        LOGGER.info("AddVatInvoiceInfo Start:" + JacksonUtil.toJson(param));
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            String lenovoId = param.getLenovoId();
            String customerName = param.getCustomerName();
            String taxNo = param.getTaxNo();
            String bankName = param.getBankName();
            String accountNo = param.getAccountNo();
            String address = param.getAddress();
            String phoneNo = param.getPhoneNo();
            boolean isShard = param.getIsShard();
            boolean isNeedMerge = param.getIsNeedMerge();
            int shopId = param.getShopId();

            //判断入参是否为空
            if (isNull(lenovoId, customerName, taxNo, bankName, accountNo, address, phoneNo, isShard, param.getFaid())) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }
            String faid;
            String storeId = null;
            String type = getFaType(param.getFaid());

            if ("1".equals(type)) {
                faid = null;
            } else if ("0".equals(type)) {
                faid = param.getFaid();
            } else {
                remoteResult.setResultMsg("获取直营失败！");
                return remoteResult;
            }

//            if(param.getFaid().equals(O2oFaIdUtil.getProperty("o2ofaid"))){
//                GetStoreInfoIdParam storeInfoIdParam = new GetStoreInfoIdParam();
//                storeInfoIdParam.setFaid(param.getFaid());
//                storeInfoIdParam.setRegion(param.getRegion());
//                RemoteResult remoteResultStoreInfo = storeInfoApiService.getStoreInfoId(storeInfoIdParam);
//                if (remoteResultStoreInfo.isSuccess()) {
//                    storeId = (String) remoteResultStoreInfo.getT();
//                }else {
//                    remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
//                    remoteResult.setResultMsg("获取storeID错误");
//                    return remoteResult;
//                }
//            }

            //验证位数，转大写
            Pattern pattern = Pattern.compile("^.{15}$|^.{18}$|^.{20}$");
            Matcher matcher = pattern.matcher(taxNo);
            if (matcher.matches()) {
                taxNo = taxNo.toUpperCase();
            } else {
                return remoteResult;
            }

            //判断customername,taxno 是否有单独存在的
//            List<VatInvoice> vatInvoiceListTmp = vatInvoiceMapper.getVatInvoiceInfo(customerName, taxNo, type);
//            if (CollectionUtils.isNotEmpty(vatInvoiceListTmp)) {
//                remoteResult.setResultCode(ErrorUtils.ERR_CODE_CUSTOMERNAME_TAXNO_EXIST);
//                remoteResult.setResultMsg("公司名和税号不匹配，请核对后再试！");
//                return remoteResult;
//            }

            VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceBySelected(new VatInvoice(customerName, taxNo, bankName, accountNo, address, phoneNo, type, faid, storeId));
            Long id = null;
            if (vatInvoice == null) {
                //新建增值税发票
                vatInvoice = new VatInvoice();
                vatInvoice.setAddress(address);
                vatInvoice.setAccountno(accountNo);
                vatInvoice.setBankname(bankName);
                vatInvoice.setTaxno(taxNo);
                vatInvoice.setCustomername(customerName);
                vatInvoice.setPhoneno(phoneNo);
                vatInvoice.setCreateby(lenovoId);
                vatInvoice.setIsshared(isShard ? 1 : 0);
                vatInvoice.setIsNeedMerge(isNeedMerge ? 1 : 0);
                vatInvoice.setShopid(shopId);
                vatInvoice.setFaid(param.getFaid());
                vatInvoice.setType(type);
                vatInvoice.setStoresid(storeId);

                if (isShard) {
                    vatInvoice.setShardedby(lenovoId);
                    vatInvoice.setShardedtime(new Date());
                }
                long rows = vatInvoiceMapper.insertVatInvoiceInfo(vatInvoice);
                if (rows > 0) {
                    //写映射表
                    MemberVatInvoice memberVatInvoice = memberVatInvoiceService.getMemberVatInvoice(vatInvoice.getId(), lenovoId);
                    if (memberVatInvoice == null) {
                        memberVatInvoiceService.insertMemberVatInvoice(vatInvoice.getId(), lenovoId, shopId, type, param.getFaid(), storeId);
                    }

                    remoteResult.setT(parseAddVatInvoiceInfoResult(vatInvoice, lenovoId));
                    remoteResult.setSuccess(true);
                    remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
                } else {
                    remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
                    remoteResult.setResultMsg("系统异常错误");
                }
            } else {
                if (isShard) {
                    Integer isSharded = vatInvoice.getIsshared();
                    id = vatInvoice.getId();
                    if (isSharded.intValue() == 0) {
                        vatInvoice.setShardedby(lenovoId);
                        vatInvoice.setIsshared(isShard ? 1 : 0);
                        vatInvoice.setShardedtime(new Date());
                        long rows = vatInvoiceMapper.updateVatInvoice(vatInvoice);
                        if (rows > 0) {
                            remoteResult.setT(parseAddVatInvoiceInfoResult(vatInvoice, lenovoId));
                            remoteResult.setSuccess(true);
                            remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
                        } else {
                            remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
                            remoteResult.setResultMsg("系统异常错误");
                        }
                    } else {
                        remoteResult.setT(parseAddVatInvoiceInfoResult(vatInvoice, lenovoId));
                        remoteResult.setSuccess(true);
                        remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);

                    }
                } else {
                    remoteResult.setT(parseAddVatInvoiceInfoResult(vatInvoice, lenovoId));
                    remoteResult.setSuccess(true);
                    remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
                }
                MemberVatInvoice memberVatInvoice = memberVatInvoiceService.getMemberVatInvoice(vatInvoice.getId(), lenovoId);
                if (memberVatInvoice == null) {
                    memberVatInvoiceService.insertMemberVatInvoice(vatInvoice.getId(), lenovoId, shopId, type, param.getFaid(), storeId);
                }
            }

        } catch (Exception e) {
            remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
            remoteResult.setResultMsg("系统异常错误");
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("AddVatInvoiceInfo End:{}", JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult checkVatInvoiceInfo(String id, String lenovoId, String region, Tenant tenant) {
        LOGGER.info("CheckVatInvoiceInfo Start:{},{},{}" + id, lenovoId, region);
        RemoteResult remoteResult = new RemoteResult(false);
        String storeId = null;
        try {
            //判断入参是否为空
            if (isNull(id, lenovoId)) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }

            Long zid = Long.valueOf(id);
            VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(zid);
            if (vatInvoice == null) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_NOTEXIST_VAT);
                remoteResult.setResultMsg("增值税发票信息不存在或未通过审核或未共享");
                return remoteResult;
            }

            //检查是否有映射关系
            MemberVatInvoice memberVatInvoice = memberVatInvoiceService.getMemberVatInvoice(zid, lenovoId);
            if (memberVatInvoice != null) {
                remoteResult.setSuccess(true);
                remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);
                remoteResult.setT(parseGetVatInvoiceInfoResult(vatInvoice, lenovoId));

                //补全门店
                if (vatInvoice.getFaid().equals(O2oFaIdUtil.getProperty("o2ofaid"))) {
                    if (memberVatInvoice.getStoresid() == null || "".equals(memberVatInvoice.getStoresid())) {
                        GetStoreInfoIdParam storeInfoIdParam = new GetStoreInfoIdParam();
                        storeInfoIdParam.setFaid(vatInvoice.getFaid());
                        storeInfoIdParam.setRegion(region);
                        RemoteResult remoteResultStoreInfo = storeInfoApiService.getStoreInfoId(storeInfoIdParam);
                        if (remoteResultStoreInfo.isSuccess()) {
                            storeId = (String) remoteResultStoreInfo.getT();
                        } else {
                            remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                            remoteResult.setResultMsg("获取storeID错误");
                            return remoteResult;
                        }

                        VatInvoice vat = vatInvoiceMapper.getVatInvoiceBySelected(new VatInvoice(vatInvoice.getCustomername(), vatInvoice.getTaxno(), vatInvoice.getBankname(), vatInvoice.getAccountno(), vatInvoice.getAddress(), vatInvoice.getPhoneno(), vatInvoice.getType(), vatInvoice.getFaid(), storeId));
                        if (vat != null) {
                            memberVatInvoiceService.delVatInvoice(id, lenovoId);
                            remoteResult.setT(parseGetVatInvoiceInfoResult(vat, lenovoId));
                        } else {
                            memberVatInvoiceService.updateVatInvoice(id, lenovoId, vatInvoice.getFaid(), storeId);
                        }
                    }
                }
            } else {
                remoteResult.setSuccess(false);
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_NOTEXIST_VAT);

            }
        } catch (Exception e) {
            remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
            remoteResult.setResultMsg("系统异常错误");
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("CheckVatInvoiceInfo End:" + JacksonUtil.toJson(remoteResult));

        return remoteResult;
    }

    @Override
    public RemoteResult changeVatInvoiceState(String id, boolean isThrough, Tenant tenant) {
        LOGGER.info("ChangeVatInvoiceState Start:增值发票{}审核状态：{}", id, isThrough);
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            //判断入参是否为空
            if (isNull(id)) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }
            long rows = vatInvoiceMapper.updateVatInvoiceCheckState(id, isThrough ? 2 : 3);
            if (rows > 0) {
                remoteResult.setSuccess(true);
                remoteResult.setResultMsg("update Success!~");
                return remoteResult;
            }
        } catch (Exception e) {
            remoteResult.setResultCode(ErrorUtils.SYSTEM_UNKNOWN_EXCEPTION);
            remoteResult.setResultMsg("系统异常错误");
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("ChangeVatInvoiceState End:" + JacksonUtil.toJson(remoteResult));
        remoteResult.setResultMsg("update Failure!~");
        return remoteResult;
    }


    @Override
    public RemoteResult queryVatInvoiceInfo(String id) {
        LOGGER.info("QueryVatInvoiceInfo Start:获取增值税发票", id);
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            //判断入参是否为空
            if (isNull(id)) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }
            VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceInfoById(Long.valueOf(id));
            if (vatInvoice != null) {
                remoteResult.setSuccess(true);
                remoteResult.setT(vatInvoice);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info("QueryVatInvoiceInfo end:返回", JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }


    public String getFaType(String faType) {
        if ("0".equals(faType) || "3".equals(faType)) {
            return "1"; //直营
        } else {
            return "0";//非直营
        }
    }

    @Override
    public RemoteResult<Boolean> throwVatInvoice2BTCP(String zids) {
        RemoteResult<Boolean> remoteResult = new RemoteResult<Boolean>(false);
        LOGGER_BTCP.info("ThrowVatInvoice2BTCP zids:{}", zids);
        try {
            if (!Strings.isNullOrEmpty(zids)) {
                String[] ids = zids.split(",");
                for (int i = 0; i < ids.length; i++) {
                    String zid = ids[i];
                    //获取可抛送订单列表
                    List<VathrowBtcp> btcpList = vathrowBtcpMapper.getVatInvoice2BtcpList(zid);
                    if (CollectionUtils.isNotEmpty(btcpList)) {
                        vatInvoiceService.throwBTCP(btcpList);
                    }
                }
            }
            remoteResult.setT(true);
        } catch (Exception e) {
            LOGGER_BTCP.error(e.getMessage(), e);
        }
        return remoteResult;
    }

    @Override
    public RemoteResult<List<VatInvoice>> queryVatInvoiceInfo(List<String> listZid) {
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            //判断入参是否为空
            if (CollectionUtils.isEmpty(listZid)) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listZid.size() - 1; i++) {
                sb.append(listZid.get(i)).append(",");
            }
            sb.append(listZid.get(listZid.size() - 1));

            List<VatInvoice> list = vatInvoiceMapper.queryVatInvoiceInfo(sb.toString());

            remoteResult.setSuccess(true);
            remoteResult.setT(list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return remoteResult;
    }


}
