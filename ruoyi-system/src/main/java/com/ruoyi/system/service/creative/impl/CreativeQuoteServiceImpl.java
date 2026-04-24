package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeQuote;
import com.ruoyi.system.mapper.creative.CreativeQuoteMapper;
import com.ruoyi.system.service.creative.ICreativeQuoteService;

@Service
public class CreativeQuoteServiceImpl implements ICreativeQuoteService
{
    @Autowired
    private CreativeQuoteMapper creativeQuoteMapper;

    @Override
    public CreativeQuote selectCreativeQuoteByQuoteId(Long quoteId)
    {
        return creativeQuoteMapper.selectCreativeQuoteByQuoteId(quoteId);
    }

    @Override
    public List<CreativeQuote> selectCreativeQuoteList(CreativeQuote creativeQuote)
    {
        return creativeQuoteMapper.selectCreativeQuoteList(creativeQuote);
    }

    @Override
    public int insertCreativeQuote(CreativeQuote creativeQuote)
    {
        return creativeQuoteMapper.insertCreativeQuote(creativeQuote);
    }

    @Override
    public int updateCreativeQuote(CreativeQuote creativeQuote)
    {
        return creativeQuoteMapper.updateCreativeQuote(creativeQuote);
    }

    @Override
    public int deleteCreativeQuoteByQuoteId(Long quoteId)
    {
        return creativeQuoteMapper.deleteCreativeQuoteByQuoteId(quoteId);
    }

    @Override
    public int deleteCreativeQuoteByQuoteIds(Long[] quoteIds)
    {
        return creativeQuoteMapper.deleteCreativeQuoteByQuoteIds(quoteIds);
    }
}
