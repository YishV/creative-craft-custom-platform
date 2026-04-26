package com.ruoyi.system.domain.creative;

import java.math.BigDecimal;

/**
 * 创作者主页聚合视图：档案 + 关键统计。
 * 仅在创作者档案 audit_status=approved 且 status=0 时返回非零统计；
 * 其余状态用于在前端展示申请进度（pending/rejected）。
 */
public class CreativeCreatorProfile
{
    private CreativeCreator creator;
    private long productCount;
    private long onShelfProductCount;
    private long pendingQuoteCount;
    private long activeOrderCount;
    private long completedOrderCount;
    private BigDecimal completedRevenue = BigDecimal.ZERO;

    public CreativeCreator getCreator()
    {
        return creator;
    }

    public void setCreator(CreativeCreator creator)
    {
        this.creator = creator;
    }

    public long getProductCount()
    {
        return productCount;
    }

    public void setProductCount(long productCount)
    {
        this.productCount = productCount;
    }

    public long getOnShelfProductCount()
    {
        return onShelfProductCount;
    }

    public void setOnShelfProductCount(long onShelfProductCount)
    {
        this.onShelfProductCount = onShelfProductCount;
    }

    public long getPendingQuoteCount()
    {
        return pendingQuoteCount;
    }

    public void setPendingQuoteCount(long pendingQuoteCount)
    {
        this.pendingQuoteCount = pendingQuoteCount;
    }

    public long getActiveOrderCount()
    {
        return activeOrderCount;
    }

    public void setActiveOrderCount(long activeOrderCount)
    {
        this.activeOrderCount = activeOrderCount;
    }

    public long getCompletedOrderCount()
    {
        return completedOrderCount;
    }

    public void setCompletedOrderCount(long completedOrderCount)
    {
        this.completedOrderCount = completedOrderCount;
    }

    public BigDecimal getCompletedRevenue()
    {
        return completedRevenue;
    }

    public void setCompletedRevenue(BigDecimal completedRevenue)
    {
        this.completedRevenue = completedRevenue;
    }
}
