package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeQuote;

public interface ICreativeQuoteService
{
    CreativeQuote selectCreativeQuoteByQuoteId(Long quoteId);

    List<CreativeQuote> selectCreativeQuoteList(CreativeQuote creativeQuote);

    int insertCreativeQuote(CreativeQuote creativeQuote);

    int updateCreativeQuote(CreativeQuote creativeQuote);

    int deleteCreativeQuoteByQuoteId(Long quoteId);

    int deleteCreativeQuoteByQuoteIds(Long[] quoteIds);
}
