package com.lenovo.invoice.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.base.Strings;
import com.lenovo.invoice.api.CommonInvoiceService;
import com.lenovo.invoice.api.InvoiceApiService;
import com.lenovo.invoice.common.CacheConstant;
import com.lenovo.invoice.common.utils.*;
import com.lenovo.invoice.dao.ChangeInvoiceHistoryMapper;
import com.lenovo.invoice.dao.MemberVatInvoiceMapper;
import com.lenovo.invoice.dao.VatInvoiceMapper;
import com.lenovo.invoice.dao.VathrowBtcpMapper;
import com.lenovo.invoice.domain.*;
import com.lenovo.invoice.domain.param.*;
import com.lenovo.invoice.domain.result.*;
import com.lenovo.invoice.service.BaseService;
import com.lenovo.invoice.service.MemberVatInvoiceService;
import com.lenovo.invoice.service.VatInvoiceService;
import com.lenovo.invoice.service.message.param.ThrowStatusParam;
import com.lenovo.invoice.service.redisObject.RedisObjectManager;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.arch.framework.domain.Tenant;
import com.lenovo.m2.arch.tool.util.StringUtils;
import com.lenovo.m2.buy.order.middleware.api.OrderInvoiceService;
import com.lenovo.m2.buy.order.middleware.domain.btcp.IncreaseOrderRequest;
import com.lenovo.m2.buy.order.middleware.domain.param.InvoiceReviewParam;
import com.lenovo.m2.ordercenter.soa.api.query.order.OrderDetailService;
import com.lenovo.m2.ordercenter.soa.domain.forward.DeliveryAddress;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.ordercenter.soa.domain.forward.Main;
import com.lenovo.m2.stock.soa.api.service.StoreInfoApiService;
import com.lenovo.m2.stock.soa.domain.param.GetStoreInfoIdParam;
import com.lenovo.my.common.utils.ErrorUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
    private static final Logger LOGGER_THROWSTATUS = LoggerFactory.getLogger("com.lenovo.invoice.customer.order.throwStatus");
    private static final Logger LOGGER_AUTOCHECKINVOICE = LoggerFactory.getLogger("com.lenovo.invoice.worker.AutoCheckInvoice");


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
    private PropertiesConfig getInvoiceTypes;
    @Autowired
    private OrderInvoiceService orderInvoiceService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private CommonInvoiceService commonInvoiceService;
    @Autowired
    private ChangeInvoiceHistoryMapper changeInvoiceHistoryMapper;
    @Autowired
    private EmailUtil emailUtil;

    @Override
    public String getType(String faid, String faType) {
        String type = null;
        String cacheKey = CacheConstant.CACHE_PREFIX_INIT_FAID + faid;
        if (redisObjectManager.existsKey(cacheKey)) {//获取fatype,没有增加缓存
            type = redisObjectManager.getString(cacheKey);
        } else {
            type = getFaType(faType);
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
    public PageModel2<VathrowBtcp> getOrderListByZidPage(PageQuery pageQuery, Map map) {
        List<VathrowBtcp> vathrowBtcpList = null;
        try {
            int count = vathrowBtcpMapper.getOrderListByZidPageCount(map);
            pageQuery.setTotalCount(count);
            if (pageQuery.getTotalCount() == 0) {
                PageModel2<VathrowBtcp> pageModel2 = new PageModel2<VathrowBtcp>(pageQuery, new ArrayList<VathrowBtcp>());
                return pageModel2;
            }

            int pageIndex = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
            int pageSize = pageQuery.getPageSize();
            map.put("pageIndex", pageIndex);//0
            map.put("pageSize", pageSize);//10
            vathrowBtcpList = vathrowBtcpMapper.getOrderListByZidPage(map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new PageModel2<VathrowBtcp>(pageQuery, vathrowBtcpList);
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
    public long makeUpVatInvocie(String orderCodes) {
        RemoteResult<Invoice> remoteResultInvoice = null;
        RemoteResult<Main> remoteResultMain = null;
        RemoteResult<DeliveryAddress> remoteResultDeliveryAddress = null;

        String[] o = orderCodes.split(",");
        for (String orderCode : o) {
            VathrowBtcp vathrowBtcp = new VathrowBtcp();
            try {
                long orderId = Long.parseLong(orderCode);
                //查询主单信息  获取发票类型
                remoteResultInvoice = orderDetailService.getInvoiceByOrderId(orderId);
                if (remoteResultInvoice.isSuccess()) {
                    Invoice invoice = remoteResultInvoice.getT();//发票类型1:电子票2:增票3:普票
                    LOGGER.info("makeUpVatInvocie invoice{}:", JacksonUtil.toJson(invoice));
                    if (invoice != null && invoice.getType() == 2) {
                        vathrowBtcp.setOrderStatus(3);
                        vathrowBtcp.setOrderCode(orderCode);
                        vathrowBtcp.setZid(invoice.getZid());
                        //初始化
                        int rows = vathrowBtcpMapper.insertVathrowBtcp(vathrowBtcp);
                        if (rows > 0) {
                            vathrowBtcp.setIsneedmerge(invoice.getIsNeedMerge());
                            vathrowBtcp.setOrderCode(orderId + "");
                            //获取订单相关信息
                            remoteResultMain = orderDetailService.getMainById(orderId);
                            remoteResultDeliveryAddress = orderDetailService.getDeliveryAddressByOrderId(orderId, 1);//1代表是发票地址，0代表是货的地址
                            LOGGER.info("remoteResultMain:{}", JacksonUtil.toJson(remoteResultMain));
                            LOGGER.info("remoteResultDeliveryAddress:{}", JacksonUtil.toJson(remoteResultDeliveryAddress));
                            if (remoteResultMain.isSuccess() && remoteResultDeliveryAddress.isSuccess()) {
                                Main main = remoteResultMain.getT();
                                if (main != null) {
                                    vathrowBtcp.setOutid(main.getOutId());
                                    vathrowBtcp.setMembercode(main.getMemberCode());
                                }
                                DeliveryAddress deliveryAddress = remoteResultDeliveryAddress.getT();
                                LOGGER.info("makeUpVatInvocie deliveryAddress:", JacksonUtil.toJson(deliveryAddress));
                                if (deliveryAddress != null) {
                                    //设置收货信息
                                    vathrowBtcp.setName(deliveryAddress.getName());//收货人姓名
                                    vathrowBtcp.setProvinceid(deliveryAddress.getProvinceId());//省份编号
                                    vathrowBtcp.setCity(deliveryAddress.getCity());//市名称
                                    vathrowBtcp.setCounty(deliveryAddress.getCounty());//区县名称
                                    vathrowBtcp.setAddress(deliveryAddress.getAddress());//详细地址
                                    vathrowBtcp.setPhone(deliveryAddress.getPhone());//联系电话
                                    vathrowBtcp.setZip(deliveryAddress.getZip());//邮编
                                }
                                //设置增票信息
                                String shopid = invoice.getTenant().getShopId() + "";
                                String zid = invoice.getZid();
                                vathrowBtcp.setZid(zid);
                                vathrowBtcp.setThrowingStatus(0);

                                VatInvoice vatInvoice = vatInvoiceService.getVatInvoiceByZid(zid, shopid);
                                if (vatInvoice != null) {
                                    vathrowBtcp.setTitle(vatInvoice.getCustomername());//发票抬头
                                    vathrowBtcp.setTaxpayeridentity(vatInvoice.getTaxno());//税号
                                    vathrowBtcp.setBankno(vatInvoice.getAccountno());//开户账号
                                    vathrowBtcp.setDepositbank(vatInvoice.getBankname());//开户行
                                    vathrowBtcp.setRegisteraddress(vatInvoice.getAddress());//注册地址
                                    vathrowBtcp.setRegisterphone(vatInvoice.getPhoneno());//电话
                                }
                                rows = vathrowBtcpMapper.updateVathrowbtcp(vathrowBtcp);
                                if (rows > 0) {
                                    //更新增票状态
                                    vatInvoiceService.updateVatInvoiceIsvalid(zid, shopid + "");
                                }
                                LOGGER.info("makeUpVatInvocie  VathrowBtcp:{},{}", JacksonUtil.toJson(vathrowBtcp), rows);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }


        return 0;
    }

    @Override
    public int updateThrowingStatus(String orderCode, int status) {
        LOGGER_THROWSTATUS.info("ThrowStatusMessageCustomer Start:{{},{}", orderCode, status);
        int rows = 0;
        try {
            rows = vathrowBtcpMapper.updateThrowingStatus(orderCode, status);
            LOGGER.info("ThrowStatusMessageCustomer End:{},{}", orderCode, rows);
        } catch (Exception e) {
            LOGGER_THROWSTATUS.error(e.getMessage(), e);
        }
        return rows;
    }

    @Override
    public long updateVatInvoice(UpdateVatInvoiceBatchParam param) {
        long rows = 0;
        try {
            rows = vathrowBtcpMapper.updateVatInvoice(param);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return rows;
    }

    @Override
    public long btcpSyncVatInvoice(IncreaseOrderRequest increaseOrderRequest) {

        try {
            int status = increaseOrderRequest.getStatus();
            if (IncreaseOrderRequest.SUCCEED != status) {
                status = InvoiceReviewParam.REVIEW_STATUS_REJECTED;
            } else {
                status = InvoiceReviewParam.REVIEW_STATUS_ACCEPT;
            }

            long orderId = 0;

            RemoteResult<Main> remoteResult = orderInvoiceService.getOrderInvoiceDetail(increaseOrderRequest.getBtcpSO());
            LOGGER_BTCP.info("btcpSyncVatInvoice:{}", JacksonUtil.toJson(remoteResult));
            if (remoteResult.isSuccess()) {
                Main main = remoteResult.getT();
                orderId = main.getId();

                InvoiceReviewParam invoiceReviewParam = new InvoiceReviewParam();
                invoiceReviewParam.setOrderId(orderId);
                invoiceReviewParam.setReviewStatus(status);
                invoiceReviewParam.setFailureReason(increaseOrderRequest.getReason());
                orderInvoiceService.updateInvoiceReviewStatus(invoiceReviewParam);

                updateThrowingStatus(orderId + "", status == 1 ? 3 : (status == 2 ? 1 : 2));
                RemoteResult<Invoice> remoteResultInvoice = orderDetailService.getInvoiceByOrderId(orderId);
                LOGGER_BTCP.info("btcpSyncVatInvoice:remoteResultInvoice{}", JacksonUtil.toJson(remoteResultInvoice));

                if (remoteResultInvoice.isSuccess()) {
                    Invoice invoice = remoteResultInvoice.getT();
                    changeVatInvoiceState(invoice.getZid(), status == 1 ? true : false, null);
                }

            }

        } catch (Exception e) {
            LOGGER_BTCP.error(e.getMessage(), e);
        }
        return 0;
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
            String type = getType(faid, param.getFaType());
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
                    LOGGER.info("####:" + JacksonUtil.toJson(memberVatInvoiceList));
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
            } else {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_VATINVOICE_NOT_EXIST);
                remoteResult.setResultMsg("增票信息不存在");
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
        String type = getType(faid, param.getFaType());

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
    public RemoteResult addVatInvoiceInfoForChange(AddVatInvoiceInfoParam param, Tenant tenant) {
        LOGGER.info("AddVatInvoiceInfoForChange Start:" + JacksonUtil.toJson(param));
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            String lenovoId = param.getLenovoId();
            String customerName = param.getCustomerName().replace(")","）").replace("(","（");;
            String taxNo = param.getTaxNo();
            String bankName = param.getBankName();
            String accountNo = param.getAccountNo();
            String address = param.getAddress();
            String phoneNo = param.getPhoneNo();

            int shopId = param.getShopId();

            //判断入参是否为空
            if (isNull(lenovoId, customerName, taxNo, bankName, accountNo, address, phoneNo, param.getFaid())) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_COM_REQURIE);
                remoteResult.setResultMsg("必填的参数错误");
                return remoteResult;
            }
            String faid;
            String storeId = null;
            String type = getFaType(param.getFaType());

            if ("1".equals(type)) {
                faid = null;
            } else if ("0".equals(type)) {
                faid = param.getFaid();
            } else {
                remoteResult.setResultMsg("获取直营失败！");
                return remoteResult;
            }

            //验证位数，转大写
            Pattern pattern = Pattern.compile("^.{15}$|^.{18}$|^.{20}$");
            Matcher matcher = pattern.matcher(taxNo);
            if (matcher.matches()) {
                taxNo = taxNo.toUpperCase();
            } else {
                remoteResult.setResultMsg("税号格式错误");
                return remoteResult;
            }

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
                vatInvoice.setIsshared(0);
                vatInvoice.setIsNeedMerge(0);
                vatInvoice.setIsvalid(1);
                vatInvoice.setIscheck(1);
                vatInvoice.setShopid(shopId);
                vatInvoice.setFaid(param.getFaid());
                vatInvoice.setType(type);
                vatInvoice.setStoresid(storeId);

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

                remoteResult.setT(parseAddVatInvoiceInfoResult(vatInvoice, lenovoId));
                remoteResult.setSuccess(true);
                remoteResult.setResultCode(ErrorUtils.INVOICE_SUCCESS);

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
        LOGGER.info("AddVatInvoiceInfoForChange End:{}", JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public RemoteResult addVatInvoiceInfo(AddVatInvoiceInfoParam param, Tenant tenant) {
        LOGGER.info("AddVatInvoiceInfo Start:" + JacksonUtil.toJson(param));
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            String lenovoId = param.getLenovoId();
            String customerName = param.getCustomerName().replace(")","）").replace("(","（");;
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
            String type = getFaType(param.getFaType());

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
            List<VatInvoice> vatInvoiceListTmp = vatInvoiceMapper.getVatInvoiceInfo(customerName, taxNo, type);
            if (CollectionUtils.isNotEmpty(vatInvoiceListTmp)) {
                remoteResult.setResultCode(ErrorUtils.ERR_CODE_CUSTOMERNAME_TAXNO_EXIST);
                remoteResult.setResultMsg("已有相同的税号或抬头的发票，如需修改请联系客服！");
                return remoteResult;
            }

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
                //用户临时决定是否需要合并
                vatInvoice.setIsNeedMerge(isNeedMerge ? 1 : 0);
                vatInvoiceMapper.updateVatInvoice(vatInvoice);
                //之前未共享 再来可以共享，反之否
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
    public RemoteResult<Boolean> throwVatInvoice2BTCP(String zids, String orderCodes) {
        RemoteResult<Boolean> remoteResult = new RemoteResult<Boolean>(false);
        LOGGER_BTCP.info("ThrowVatInvoice2BTCP zid:{},orderCodes", zids, orderCodes);
        try {
            if (!Strings.isNullOrEmpty(orderCodes)) {
                List<VathrowBtcp> btcpList = vathrowBtcpMapper.getVatInvoice2BtcpListByOrderCode(orderCodes);
                if (CollectionUtils.isNotEmpty(btcpList)) {
                    vatInvoiceService.throwBTCP(btcpList);
                }
            }
            if (!Strings.isNullOrEmpty(zids)) {
                String[] ids = zids.split(",");
                for (int i = 0; i < ids.length; i++) {
                    String zid = ids[i];
                    //获取可抛送订单列表
                    List<VathrowBtcp> btcpList = vathrowBtcpMapper.getVatInvoice2BtcpListByZid(zid);
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

    @Override
    public RemoteResult<List<FaInvoiceResult>> getInvoiceTypes(GetInvoiceTypeParam getInvoiceTypeParam) {
        LOGGER_BTCP.info("getInvoiceTypes 参数" + JacksonUtil.toJson(getInvoiceTypeParam));
        RemoteResult<List<FaInvoiceResult>> listRemoteResult = new RemoteResult<List<FaInvoiceResult>>(false);
        try {
            if (getInvoiceTypeParam.getBu() == 140) {//
                listRemoteResult.setResultMsg("不能开具发票");
                return listRemoteResult;
            }
            List<FaInvoiceResult> faInvoiceResults = new ArrayList<FaInvoiceResult>();
            List<FaData> faDatas = getInvoiceTypeParam.getFaDatas();
            for (int i = 0; i < faDatas.size(); i++) {
                FaInvoiceResult faInvoiceResult = new FaInvoiceResult();
                faInvoiceResult.setFaid(faDatas.get(i).getFaid());
                if(getInvoiceTypes.getOpenDz().equals("no")){
                    faInvoiceResult.setInvoiceList(getInvoiceTypes(getInvoiceTypeParam.getShopId(),
                            getInvoiceTypeParam.getSalesType(),
                            faDatas.get(i).getFatype(),
                            faDatas.get(i).getFaid(),
                            getInvoiceTypes.getOpenO2O(),
                            getInvoiceTypes.getOpenZy()));
                }else {
                    faInvoiceResult.setInvoiceList(getInvoiceTypesNOdz(getInvoiceTypeParam.getShopId(),
                            getInvoiceTypeParam.getSalesType(),
                            faDatas.get(i).getFatype(),
                            faDatas.get(i).getFaid(),
                            getInvoiceTypes.getOpenO2O(),
                            getInvoiceTypes.getOpenZy()));
                }
                faInvoiceResults.add(faInvoiceResult);
            }
            listRemoteResult.setSuccess(true);
            listRemoteResult.setT(faInvoiceResults);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER_BTCP.error("getInvoiceTypes 异常" + e.getMessage());
        }
        LOGGER_BTCP.info("getInvoiceTypes 返回" + JacksonUtil.toJson(listRemoteResult));
        return listRemoteResult;
    }

    @Override
    public RemoteResult<List<FaInvoiceResult>> getInvoiceTypes(GetInvoiceTypeParam getInvoiceTypeParam, Tenant tenant) {
        LOGGER_BTCP.info("getInvoiceTypes Tenant 参数" + JacksonUtil.toJson(tenant));
        if (tenant != null && tenant.getShopId() != null) {
            getInvoiceTypeParam.setShopId(tenant.getShopId());
        }
        return getInvoiceTypes(getInvoiceTypeParam);
    }

    @Override
    public RemoteResult<ConfigurationInformation> getConfigurationInformation(GetCiParam getCiParam, Tenant tenant) {
        LOGGER_BTCP.info("getConfigurationInformation 参数" + JacksonUtil.toJson(getCiParam));
        RemoteResult<ConfigurationInformation> result = new RemoteResult<ConfigurationInformation>(false);
        ConfigurationInformation information = new ConfigurationInformation();
        try {
            GetInvoiceTypeParam getInvoiceTypeParam = new GetInvoiceTypeParam();
            BeanUtils.copyProperties(getInvoiceTypeParam, getCiParam);
            information.setFaInvoiceResults(getInvoiceTypes(getInvoiceTypeParam, tenant).getT());
            information.setPayment(getPaymentType(getCiParam, tenant));
            information.setDeliverGoods(getDeliverGoods(tenant.getShopId()));
            result.setSuccess(true);
            result.setT(information);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER_BTCP.info("getConfigurationInformation 返回" + JacksonUtil.toJson(result));
        return result;
    }

    @Override
    public void autoCheckInvoice() {
        try {
            LOGGER_AUTOCHECKINVOICE.info("AutoCheckInvoice Start:");
            //以防重复审核
            List<String> listNotCheck = new ArrayList<String>();
            //获取待处理列表
            List<VatInvoice> listVatInvoice = vatInvoiceMapper.getAutoCheckInvoice();
            for (VatInvoice vatInvoice : listVatInvoice) {
                String customername = vatInvoice.getCustomername();
                if (!listNotCheck.contains(customername)) {
                    String taxNo = vatInvoice.getTaxno();
                    String autoTaxNo = AutoCheckInvoiceUtil.getTaxNo(customername);
                    if (Strings.isNullOrEmpty(autoTaxNo)) {
                        //自动审核失败
                        vatInvoice.setCheckBy("admin_check");
                        vatInvoice.setIscheck(4);
                        long rows = vatInvoiceMapper.updateVatInvoiceAutoCheck(vatInvoice);
                        if (rows > 0) {
                            //未自动审核成功，发邮件
                            //拼邮件1.下单账号，2.发票抬头，3.识别码类型，4.税号，5.发票类型，6.订单号，7.收货人，8.收货电话。税务登记证（15位）统一社会信用代码（18位）
                            Integer taxNoType = vatInvoice.getTaxNoType();
                            String taxNoTypeStr;
                            if (taxNoType == 1) {
                                taxNoTypeStr = "税务登记证（15位）";
                            } else if (taxNoType == 3) {
                                taxNoTypeStr = "无（政府机构，事业单位，非企业单位）";
                            } else {
                                taxNoTypeStr = "统一社会信用代码（18位）";
                            }
                            String typeStr;
                            Integer type = vatInvoice.getInvoiceType();
                            if (type == 0) {
                                typeStr = "电子票";
                            } else {
                                typeStr = "普通发票";
                            }
                            String content = "您好，有待审核发票请您尽快去审核，信息如下：" + "发票抬头:" + vatInvoice.getCustomername()
                                    + ";识别码类型:" + taxNoTypeStr + ";税号:" + vatInvoice.getTaxno() + ";发票类型:" + typeStr + "。";
                            String title = "发票审核";
                            emailUtil.sendEmail(title, content);
                        }
                    } else {
                        if (!autoTaxNo.equals(taxNo)) {
                            //设置history表
                            changeInvoiceHistoryMapper.insertChangeInvoiceHistory(new ChangeInvoiceHistory(vatInvoice.getId(), vatInvoice.getCustomername(), vatInvoice.getTaxno(), autoTaxNo));
                            vatInvoice.setTaxno(autoTaxNo);
                            int len = autoTaxNo.length();
                            //识别码类型，1是15、20位，2是18位，3是无
                            vatInvoice.setTaxNoType(len == 15 || len == 20 ? 1 : 2);
                        }
                        vatInvoice.setCheckBy("admin_check");
                        vatInvoice.setIscheck(1);
                        long rows = vatInvoiceMapper.updateVatInvoiceAutoCheck(vatInvoice);
                        if (rows > 0) {
                            listNotCheck.add(customername);
                            commonInvoiceService.deleteTheSameTitleInvoice(customername, vatInvoice.getId());
                        }

                    }
                }
            }
        } catch (Exception e) {
            LOGGER_AUTOCHECKINVOICE.error(e.getMessage(), e);
        }
    }

    public Payment getPaymentType(GetCiParam getCiParam, Tenant tenant) {
        if (tenant.getShopId() == 9) {
            return new Payment();
        }
        if (getCiParam.getSalesType() == 98) {
            Payment payment = new Payment();
            payment.setDefaultType(PaymentType.ZXZF);
            payment.setPaymentTypes(Arrays.asList(new PaymentType[]{PaymentType.ZXZF, PaymentType.HDFK}));
            return payment;
        }
        if (tenant.getShopId() == 8) {
            if ((getCiParam.getFaDatas().size() == 1 && getCiParam.getFaDatas().get(0).getFatype() == 7) || getCiParam.getBigDecimal().doubleValue() > 50000) {//只有一个fa并且faType=SMB_ZY_ALL()直营总代  ：线下转账并默认
                Payment payment = new Payment();
                payment.setDefaultType(PaymentType.XXZZ);
                payment.setPaymentTypes(Arrays.asList(new PaymentType[]{PaymentType.XXZZ}));
                return payment;
            }
            Payment payment = new Payment();
            payment.setDefaultType(PaymentType.XXZZ);
            payment.setPaymentTypes(Arrays.asList(new PaymentType[]{PaymentType.XXZZ, PaymentType.ZXZF}));
            return payment;
        }

        if (tenant.getShopId() == 14 && "on".equals(getInvoiceTypes.getHuiShangZF())) {//惠商
            Payment payment = new Payment();
            payment.setDefaultType(PaymentType.ZXZF);
            payment.setPaymentTypes(Arrays.asList(new PaymentType[]{PaymentType.ZXZF, PaymentType.XXZZ}));
            return payment;
        }
        if (tenant.getShopId() == 16) {//印度摩托
            Payment payment = new Payment();
            payment.setDefaultType(PaymentType.ZXZF_YD);
            payment.setPaymentTypes(Arrays.asList(new PaymentType[]{PaymentType.ZXZF_YD}));
            return payment;
        }
        Payment payment = new Payment();
        payment.setDefaultType(PaymentType.ZXZF);
        payment.setPaymentTypes(Arrays.asList(new PaymentType[]{PaymentType.ZXZF}));
        return payment;
    }

    public InvoiceList getInvoiceTypes(int shopId, int salesType, int fatype, String faid, String openO2O, String openZy) {
        if (shopId == 9) {
            return null;
        }
        HashSet<Integer> fatypesets = new HashSet<Integer>();
        fatypesets.add(1);
        fatypesets.add(2);
        fatypesets.add(4);
        if (shopId == 8) {
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
        }
        if (shopId == 14) {
            return new InvoiceList(null, Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
        }
        if (salesType == 98) {//alesType=o2o && openO2OVatInvoice.eq(“on”)
            if (openO2O.equals("on")) {
                return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
            } else {
                return new InvoiceList(null, Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP}));
            }
        }
        if (salesType == 97) {//salesType=ZC_SALES(众筹)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
        }
        if ("36d0caa1-af7a-459b-b147-04b86ad25dd7".equals(faid)) {//faids.contains(zukFaid)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.DZFP}), null);
        }
        if (fatypesets.contains(fatype)) {//非直营 fatypesets.congtains(1/2/4)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}));
        }
        HashSet<Integer> smartTv = new HashSet<Integer>();
        smartTv.add(9);
        smartTv.add(10);
        if (smartTv.contains(fatype)) {//smart.Tv  fatypesets.contains(9/10)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}));
        }
        HashSet<Integer> zyFaTye = new HashSet<Integer>();
        zyFaTye.add(0);
        zyFaTye.add(3);
        if (zyFaTye.contains(fatype)) {//直营 fatypes.contains(0/3)
            if (openZy.equals("on")) {
                return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.DZFP, InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.PTFP, InvoiceType.ZZFP, InvoiceType.DZFP}));
            } else {
                return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.DZFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.DZFP}));
            }
        }
        if (shopId == 15 || fatype == 5) {
            return null;
        }

        return null;
    }

    public InvoiceList getInvoiceTypesNOdz(int shopId, int salesType, int fatype, String faid, String openO2O, String openZy) {
        if (shopId == 9) {
            return null;
        }
        HashSet<Integer> fatypesets = new HashSet<Integer>();
        fatypesets.add(1);
        fatypesets.add(2);
        fatypesets.add(4);
        if (shopId == 8) {
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
        }
        if (shopId == 14) {
            return new InvoiceList(null, Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
        }
        if (salesType == 98) {//alesType=o2o && openO2OVatInvoice.eq(“on”)
            if (openO2O.equals("on")) {
                return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
            } else {
                return new InvoiceList(null, Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP}));
            }
        }
        if (salesType == 97) {//salesType=ZC_SALES(众筹)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP, InvoiceType.PTFP}));
        }
        if ("36d0caa1-af7a-459b-b147-04b86ad25dd7".equals(faid)) {//faids.contains(zukFaid)
            return new InvoiceList(null, null);
        }
        if (fatypesets.contains(fatype)) {//非直营 fatypesets.congtains(1/2/4)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}));
        }
        HashSet<Integer> smartTv = new HashSet<Integer>();
        smartTv.add(9);
        smartTv.add(10);
        if (smartTv.contains(fatype)) {//smart.Tv  fatypesets.contains(9/10)
            return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}));
        }
        HashSet<Integer> zyFaTye = new HashSet<Integer>();
        zyFaTye.add(0);
        zyFaTye.add(3);
        if (zyFaTye.contains(fatype)) {//直营 fatypes.contains(0/3)
            if (openZy.equals("on")) {
                return new InvoiceList(Arrays.asList(new InvoiceType[]{InvoiceType.PTFP}), Arrays.asList(new InvoiceType[]{InvoiceType.PTFP, InvoiceType.ZZFP}));
            } else {
                return new InvoiceList(null, Arrays.asList(new InvoiceType[]{InvoiceType.ZZFP}));
            }
        }
        if (shopId == 15 || fatype == 5) {
            return null;
        }

        return null;
    }

    public DeliverGoods getDeliverGoods(Integer shopid) {
        if (shopid == 16) {//印度摩托
            DeliverGoods deliverGoods = new DeliverGoods();
            deliverGoods.setDefaultType(DeliverGoodsTypeEnum.SURFACE_MODE);
            deliverGoods.setDeliverGoodsList(Arrays.asList(new DeliverGoodsTypeEnum[]{DeliverGoodsTypeEnum.AIRE_MODE, DeliverGoodsTypeEnum.SURFACE_MODE, DeliverGoodsTypeEnum.SELF_PICK}));
            return deliverGoods;
        }
        return null;
    }
}
