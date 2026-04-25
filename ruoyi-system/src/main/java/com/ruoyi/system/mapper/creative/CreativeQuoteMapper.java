package com.ruoyi.system.mapper.creative;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.creative.CreativeQuote;

public interface CreativeQuoteMapper
{
    CreativeQuote selectCreativeQuoteByQuoteId(Long quoteId);

    List<CreativeQuote> selectCreativeQuoteList(CreativeQuote creativeQuote);

    int insertCreativeQuote(CreativeQuote creativeQuote);

    int updateCreativeQuote(CreativeQuote creativeQuote);

    int deleteCreativeQuoteByQuoteId(Long quoteId);

    int deleteCreativeQuoteByQuoteIds(Long[] quoteIds);

    /**
     * 把同一个需求下、除指定报价外的所有报价批量置为目标状态。
     * 用于"选中某个报价后，其他报价自动落败"场景。
     */
    int rejectOtherQuotesByDemand(@Param("demandId") Long demandId,
                                  @Param("keepQuoteId") Long keepQuoteId,
                                  @Param("targetStatus") String targetStatus,
                                  @Param("updateBy") String updateBy);
}
