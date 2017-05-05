package com.lenovo.invoice.service.impl;

import com.lenovo.invoice.common.CacheConstant;
import com.lenovo.invoice.common.utils.JacksonUtil;
import com.lenovo.invoice.dao.MemberVatInvoiceMapper;
import com.lenovo.invoice.domain.MemberVatInvoice;
import com.lenovo.invoice.service.MemberVatInvoiceService;
import com.lenovo.invoice.service.redisObject.RedisObjectManager;
import com.lenovo.m2.arch.framework.domain.RemoteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mayan3 on 2016/7/27.
 */
@Service("memberVatInvoiceService")
public class MemberVatInvoiceServiceImpl implements MemberVatInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(MemberVatInvoiceServiceImpl.class);

    @Autowired
    private RedisObjectManager redisObjectManager;
    @Autowired
    private MemberVatInvoiceMapper memberVatInvoiceMapper;

    @Override
    public void insertMemberVatInvoice(Long id, String lenovoId, int shopId,String type,String faid,String storesid) {
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_INIT_MEMBERVATINVOICE + id + "_" + lenovoId;
            MemberVatInvoice memberVatInvoice = getMemberVatInvoice(id, lenovoId);
            if (memberVatInvoice == null) {
                memberVatInvoice = new MemberVatInvoice();
                memberVatInvoice.setInvoiceinfoid(id);
                memberVatInvoice.setLenovoid(lenovoId);
                memberVatInvoice.setShopid(shopId);
                memberVatInvoice.setType(type);
                memberVatInvoice.setFaid(faid);
                memberVatInvoice.setStoresid(storesid);
                Long rows = memberVatInvoiceMapper.insertMemberVatInvoice(memberVatInvoice);
                if (rows > 0) {
                    redisObjectManager.setString(cacheKey, JacksonUtil.toJson(memberVatInvoice));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public MemberVatInvoice getMemberVatInvoice(Long id, String lenovoId) {
        MemberVatInvoice memberVatInvoice = null;
        try {
            String cacheKey = CacheConstant.CACHE_PREFIX_INIT_MEMBERVATINVOICE + id + "_" + lenovoId;

            if (redisObjectManager.existsKey(cacheKey)) {
                memberVatInvoice = JacksonUtil.fromJson(redisObjectManager.getString(cacheKey), MemberVatInvoice.class);
            } else {
                memberVatInvoice = memberVatInvoiceMapper.getMemberVatInvoice(id, lenovoId);
                if (memberVatInvoice != null) {
                    redisObjectManager.setString(cacheKey, JacksonUtil.toJson(memberVatInvoice));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return memberVatInvoice;
    }

    @Override
    public List<MemberVatInvoice> getMemberVatInvoiceByZid(Long zid) {
        List<MemberVatInvoice> list = null;
        try {
            list = memberVatInvoiceMapper.getMemberVatInvoiceByZid(zid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Long deleteMemberVatInvoice(Long zid) {
        long rows = 0;
        try {
            rows = memberVatInvoiceMapper.deleteMemberVatInvoice(zid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rows;
    }

    @Override
    public void updateVatInvoice(String id, String lenovoId, String faid, String storesid) {
        MemberVatInvoice memberVatInvoice = null;
        try {
            memberVatInvoiceMapper.updateVatInvoice(id, lenovoId, faid, storesid);
            String cacheKey = CacheConstant.CACHE_PREFIX_INIT_MEMBERVATINVOICE + id + "_" + lenovoId;
            if (redisObjectManager.existsKey(cacheKey)) {
                memberVatInvoice = JacksonUtil.fromJson(redisObjectManager.getString(cacheKey), MemberVatInvoice.class);
                memberVatInvoice.setStoresid(storesid);
                redisObjectManager.setString(cacheKey, JacksonUtil.toJson(memberVatInvoice));
            } else {
                memberVatInvoice = memberVatInvoiceMapper.getMemberVatInvoice(Long.parseLong(id), lenovoId);
                if (memberVatInvoice != null) {
                    redisObjectManager.setString(cacheKey, JacksonUtil.toJson(memberVatInvoice));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void delVatInvoice(String id,String lenovoId) {
        MemberVatInvoice memberVatInvoice = null;
        try {
            memberVatInvoiceMapper.delVatInvoice(id);
            String cacheKey = CacheConstant.CACHE_PREFIX_INIT_MEMBERVATINVOICE + id + "_" + lenovoId;
            if (redisObjectManager.existsKey(cacheKey)) {
                redisObjectManager.delKey(cacheKey);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


}
