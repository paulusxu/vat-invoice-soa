package com.lenovo.invoice.service;

import com.lenovo.invoice.domain.MemberVatInvoice;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

import java.util.List;

/**
 * Created by mayan3 on 2016/7/27.
 */
public interface MemberVatInvoiceService {
    void insertMemberVatInvoice(Long id,String lenovoId,int shopId,String type,String faid,String storesid);
    MemberVatInvoice getMemberVatInvoice(Long id,String lenovoId) ;
    List<MemberVatInvoice> getMemberVatInvoiceByZid(Long zid);
    Long deleteMemberVatInvoice(Long zid);
    void updateVatInvoice(String id,String lenovoId,String faid,String storesid);

    void delVatInvoice(String id,String lenovoId);

}
