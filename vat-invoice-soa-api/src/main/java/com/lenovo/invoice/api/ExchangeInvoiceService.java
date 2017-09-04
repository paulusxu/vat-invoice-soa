package com.lenovo.invoice.api;

import com.lenovo.invoice.domain.ExchangeInvoiceRecord;
import com.lenovo.invoice.domain.param.ExchangeInvoiceParam;
import com.lenovo.invoice.domain.result.GetVatInvoiceInfoResult;
import com.lenovo.m2.arch.framework.domain.PageModel2;
import com.lenovo.m2.arch.framework.domain.PageQuery;
import com.lenovo.m2.arch.framework.domain.RemoteResult;

import java.util.List;

/**
 * Created by admin on 2017/3/19.
 */
public interface ExchangeInvoiceService {

    //校验是否可以换票
    public RemoteResult ifExchangeVatInvoice(String orderCode);

    //BTCP回调接口
    public RemoteResult BTCPCallback(String applyId,String code,String message);

    //换普票
    public RemoteResult exchangeToCommon(ExchangeInvoiceParam param);

    //换增票
    public RemoteResult exchangeToVat(ExchangeInvoiceParam param);

    //获取换票记录，加分页
    public RemoteResult<PageModel2<ExchangeInvoiceRecord>> getExchangeInvoiceRecordByPage(PageQuery pageQuery, ExchangeInvoiceRecord exchangeInvoiceRecord);

    //导出换票记录，必须指定时间段
    public RemoteResult<List<ExchangeInvoiceRecord>> getExchangeInvoiceRecordList(ExchangeInvoiceRecord exchangeInvoiceRecord);

    //获取换票记录详情
    public RemoteResult<ExchangeInvoiceRecord> getExchangeInvoiceRecord(String id);

    //换增票校验接口，如果存在，回显增票信息
    public RemoteResult<GetVatInvoiceInfoResult> ifVatInvoiceExist(String taxNO,String orderCode);

}
