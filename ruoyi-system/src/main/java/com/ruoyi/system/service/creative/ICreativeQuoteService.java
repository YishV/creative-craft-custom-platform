package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeQuote;

public interface ICreativeQuoteService
{
    CreativeQuote selectCreativeQuoteByQuoteId(Long quoteId);

    List<CreativeQuote> selectCreativeQuoteList(CreativeQuote creativeQuote);

    int insertCreativeQuote(CreativeQuote creativeQuote);

    int updateCreativeQuote(CreativeQuote creativeQuote);

    int deleteCreativeQuoteByQuoteId(Long quoteId);

    int deleteCreativeQuoteByQuoteIds(Long[] quoteIds);

    /**
     * 买家选中报价：
     * 1. 校验报价 / 需求当前状态合法
     * 2. 把目标报价置为 selected，其余报价置为 rejected
     * 3. 推进需求状态到 selected
     * 4. 自动生成订单（buyer = 需求 userId, seller = 报价 creatorId, amount = 报价金额）
     * 返回新创建的订单实体（含 orderId、orderNo）
     */
    CreativeOrder selectQuoteAndGenerateOrder(Long quoteId, String operator);
}
