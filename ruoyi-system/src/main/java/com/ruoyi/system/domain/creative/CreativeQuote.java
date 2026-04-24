package com.ruoyi.system.domain.creative;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

public class CreativeQuote extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long quoteId;
    private Long demandId;
    private Long creatorId;
    private BigDecimal quoteAmount;
    private Integer deliveryDays;
    private String quoteStatus;

    public Long getQuoteId()
    {
        return quoteId;
    }

    public void setQuoteId(Long quoteId)
    {
        this.quoteId = quoteId;
    }

    public Long getDemandId()
    {
        return demandId;
    }

    public void setDemandId(Long demandId)
    {
        this.demandId = demandId;
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }

    public BigDecimal getQuoteAmount()
    {
        return quoteAmount;
    }

    public void setQuoteAmount(BigDecimal quoteAmount)
    {
        this.quoteAmount = quoteAmount;
    }

    public Integer getDeliveryDays()
    {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays)
    {
        this.deliveryDays = deliveryDays;
    }

    public String getQuoteStatus()
    {
        return quoteStatus;
    }

    public void setQuoteStatus(String quoteStatus)
    {
        this.quoteStatus = quoteStatus;
    }
}
