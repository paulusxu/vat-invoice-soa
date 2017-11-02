package com.lenovo.invoice.dao;

import com.lenovo.invoice.domain.Consignee;
import com.lenovo.invoice.domain.VatInvoice;
import com.lenovo.invoice.domain.VathrowBtcp;
import com.lenovo.invoice.domain.param.UpdateVatInvoiceBatchParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VathrowBtcpMapper {

    int updateByOrderCode(@Param("orderCode") String orderCode, @Param("status") int status, @Param("msg") String msg);

    int updateConsignee(Consignee consignee);

    int insertVathrowBtcp(VathrowBtcp record);


    //根据批量增票zid获取 准备抛送btcp的列表
    List<VathrowBtcp> getVatInvoice2BtcpListByZid(String zid);

    //根据批量orderid获取 准备抛送btcp的列表
    List<VathrowBtcp> getVatInvoice2BtcpListByOrderCode(String orderCodes);

    int updateOrderStatus(@Param("orderCode") String orderCode, @Param("status") int status);

    int updateThrowingStatus(@Param("orderCode") String orderCode, @Param("status") int status);


    int updateVathrowbtcp(@Param("record") VathrowBtcp record);

    List<VathrowBtcp> getThrowBtcpList();

    List<VatInvoice> getNotThrowBtcpVatInvoicePage(Map map);

    List<VathrowBtcp> getOrderListByZidPage(Map map);

    List<VathrowBtcp> getNotThrowBtcpOrderListPage(Map map);

    int getNotThrowBtcpVatInvoiceCount(Map map);

    int getOrderListByZidPageCount(Map map);

    int getNotThrowBtcpOrderListPageCount(Map map);

    long updateZid(@Param("record") VatInvoice record);

    long updateVatInvoice(UpdateVatInvoiceBatchParam param);

    //根据订单code获取增值税发票信息
    public VathrowBtcp getVatInvoiceByOrderCode(String orderCode);

    //根据orderCode和orderStatus修改增票信息
    public int updateVatBTCP(VathrowBtcp vathrowBtcp);

    //根据orderCode删除增票和订单的映射关系记录
    public int deleteByOrderCode(String orderCode);

    //添加一条完成的增票和订单的映射
    public int addVathrowBtcp(VathrowBtcp vathrowBtcp);

}