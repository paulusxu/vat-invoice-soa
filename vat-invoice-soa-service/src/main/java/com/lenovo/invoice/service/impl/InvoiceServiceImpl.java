package com.lenovo.invoice.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.google.common.collect.Maps;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.VatInvoiceMapper;
import com.lenovo.invoice.domain.MemberVatInvoice;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import com.lenovo.invoice.service.InvoiceService;
import com.lenovo.invoice.service.MemberVatInvoiceService;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import com.lenovo.m2.ordercenter.soa.api.vat.VatApiOrderCenter;
import com.lenovo.m2.ordercenter.soa.domain.forward.Invoice;
import com.lenovo.m2.ordercenter.soa.domain.increaseorder.InvoiceOpen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mayan3 on 2016/6/28.
 */
@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);

//    @Autowired
//    @Qualifier("vatApiOrderCenter")
    private VatApiOrderCenter vatApiOrderCenter;



    @Autowired
    private VatInvoiceMapper vatInvoiceMapper;
    @Autowired
    private MemberVatInvoiceService memberVatInvoiceService;


    @Override
    public boolean checkVatInvoiceInfo(List<VatInvoice> vatInvoiceList) {
        logger.info("CheckVatInvoiceInfo Start:" + JacksonUtil.toJson(vatInvoiceList));
        try {
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
            RemoteResult remoteResult=vatApiOrderCenter.invoiceCallBackService(orderList,0);
            logger.info("CheckVatInvoiceInfo End:" + JacksonUtil.toJson(remoteResult));
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
@Override
    public boolean checkVatInvoiceInfoNew(List<Invoice> vatInvoiceList) {
        logger.info("checkVatInvoiceInfoNew Start:" + JacksonUtil.toJson(vatInvoiceList));
        try {

            RemoteResult remoteResult=vatApiOrderCenter.invoiceCallBackService(vatInvoiceList, 1);
            logger.info("checkVatInvoiceInfoNew End:" + JacksonUtil.toJson(remoteResult));
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public InvoiceOpen getIncreaseOrderList(String id, String rows, String page) {
        RemoteResult<InvoiceOpen> invoiceOpenRemoteResult=null;
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("zid", id);
            map.put("rows", rows);
            map.put("page", page);
            invoiceOpenRemoteResult= vatApiOrderCenter.queryInvoiceOpenService(map);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return (InvoiceOpen)invoiceOpenRemoteResult.getT();
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateVatInvoiceBatch(UpdateVatInvoiceBatchParam param) throws Exception {
        logger.info("updateVatInvoiceBatch 参数" + JacksonUtil.toJson(param));
        RemoteResult remoteResult = new RemoteResult(false);
        Long id = Long.parseLong(param.getIds());
        try {
            vatInvoiceMapper.insertVatInvoiceLogBatch(param);
            VatInvoice vatInvoice = vatInvoiceMapper.getVatInvoiceBySelected(new VatInvoice(param.getCustomerName(), param.getTaxNo(),
                    param.getBankName(), param.getAccountNo(), param.getAddress(), param.getPhoneNo(), param.getType(), param.getType().equals("1") ? null : param.getFaid(), null));
            if (vatInvoice != null) {
                if (vatInvoice.getId().intValue() != Integer.parseInt(param.getIds())) {
                    List<Long> list = new ArrayList<Long>();
                    list.add(id);

                    remoteResult = vatApiOrderCenter.refreshZid(list, vatInvoice.getId());
                    logger.info("updateVatInvoiceBatch[" + list.get(0) + "," + vatInvoice.getId() + "] refreshZid返回参数" + JacksonUtil.toJson(remoteResult));
                    if (remoteResult.isSuccess()) {
                        logger.info("refreshZid:" + JacksonUtil.toJson(vatInvoiceMapper.getVatInvoiceInfoById(id)));

                        long rows = vatInvoiceMapper.deleteVatInvoice(id);
                        if (rows > 0) {
                            //获取
                            List<MemberVatInvoice> listLenovoId = memberVatInvoiceService.getMemberVatInvoiceByZid(id);
                            if (CollectionUtils.isNotEmpty(listLenovoId)) {
                                for (MemberVatInvoice invoice : listLenovoId) {
                                    memberVatInvoiceService.insertMemberVatInvoice(vatInvoice.getId(), invoice.getLenovoid(),invoice.getShopid(),param.getType(),param.getFaid(),null);
                                }
                                memberVatInvoiceService.deleteMemberVatInvoice(id);
                            }
                        }
                    }
                }
            }else {
                UpdateVatInvoiceBatchParam updateParam = new UpdateVatInvoiceBatchParam();
                updateParam.setCustomerName(param.getCustomerName());
                updateParam.setTaxNo(param.getTaxNo());
                updateParam.setAccountNo(param.getAccountNo());
                updateParam.setAddress(param.getAddress());
                updateParam.setPhoneNo(param.getPhoneNo());
                updateParam.setBankName(param.getBankName());
                updateParam.setItcode(param.getItcode());
                updateParam.setIds(param.getIds());
                updateParam.setShopid(param.getShopid());
                vatInvoiceMapper.updateVatInvoiceBatch(updateParam);
            }

        } catch (Exception e) {
            logger.error("updateVatInvoiceBatch>>"+e.getMessage(), e);
            throw new Exception();
        }

        return 0;
    }


    public PageModel2<VatInvoice> getVatInvoicePage(PageQuery pageQuery, Map map) {
        int count = vatInvoiceMapper.getVatInvoiceCount(map);
        pageQuery.setTotalCount(count);
        if (pageQuery.getTotalCount() == 0) {
            PageModel2<VatInvoice> pageModel2 = new PageModel2<VatInvoice>(pageQuery, new ArrayList<VatInvoice>());
            return pageModel2;
        }
        int pageIndex = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int pageSize = pageQuery.getPageSize();
        map.put("pageIndex", pageIndex);//0
        map.put("pageSize", pageSize);//10
        List<VatInvoice> vatInvoices = vatInvoiceMapper.getVatInvoicePage(map);
        return new PageModel2<VatInvoice>(pageQuery, vatInvoices);
    }

    @Override
    public List<VatInvoice> getVatInvoiceList(String ids) {
        return vatInvoiceMapper.getVatInvoiceList(ids);
    }

    @Override
    public List<VatInvoice> getExportVatInvoiceList(Map map) {
        return vatInvoiceMapper.getExportVatInvoiceList(map);
    }

    @Override
    public RemoteResult changeInvoiceOrderMapping(List<Long> list, Long zid) {
        logger.info("ChangeInvoiceOrderMapping Start:{},{}", zid, JacksonUtil.toJson(list));
        RemoteResult remoteResult = new RemoteResult(false);
        try {
            remoteResult = vatApiOrderCenter.refreshZid(list, zid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("ChangeInvoiceOrderMapping End:", zid, JacksonUtil.toJson(remoteResult));
        return remoteResult;
    }

    @Override
    public String getInvoiceOwer(String zid, String shopid) {
        String ower = null;
        try {
            ower = vatInvoiceMapper.getInvoiceOwer(zid, shopid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ower;
    }

    @Override
    public List<VatInvoice> getVatInvoiceValidationList(Long id, String TaxNo, String CustomerName,String type,String faid) {
        return vatInvoiceMapper.getVatInvoiceValidationList(id,TaxNo,CustomerName,type,faid);
    }

    @Override
    public void updateIsvalid(String zid) {
        try {
            vatInvoiceMapper.updateIsvalid(zid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateIsCheck(String ids) {
        vatInvoiceMapper.updateIsCheck(ids);
    }



}

