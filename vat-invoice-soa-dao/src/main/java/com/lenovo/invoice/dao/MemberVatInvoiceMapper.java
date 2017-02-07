package com.lenovo.invoice.dao;


import com.lenovo.invoice.domain.MemberVatInvoice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberVatInvoiceMapper {
    List<MemberVatInvoice> getMemberVatInvoiceByLenovoId(@Param("lenovoId") String lenovoId,@Param("type") String type,@Param("faid")String faid,@Param("storesid")String storesid);
    MemberVatInvoice getMemberVatInvoice(@Param("id") Long id, @Param("lenovoId") String lenovoId);
    Long insertMemberVatInvoice(MemberVatInvoice memberVatInvoice);

    Long deleteMemberVatInvoice(Long zid);

    List<MemberVatInvoice> getMemberVatInvoiceByZid(Long zid);

    List<MemberVatInvoice> getAllMemberVatInvoice();
    List<Long> getNewZid();
    List<Long> getMappingZid(Long zid);

    int updateVatInvoice(@Param("id")String id, @Param("lenovoId")String lenovoId,@Param("faid") String faid, @Param("storesid")String storesid);

    int delVatInvoice(@Param("id")String id);
}