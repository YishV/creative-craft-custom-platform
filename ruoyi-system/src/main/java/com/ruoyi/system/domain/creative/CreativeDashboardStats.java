package com.ruoyi.system.domain.creative;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CreativeDashboardStats implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Summary summary;
    private List<TrendItem> trend;
    private List<CategoryStat> hotCategories;
    private List<ActiveCreator> activeCreators;

    public Summary getSummary()
    {
        return summary;
    }

    public void setSummary(Summary summary)
    {
        this.summary = summary;
    }

    public List<TrendItem> getTrend()
    {
        return trend;
    }

    public void setTrend(List<TrendItem> trend)
    {
        this.trend = trend;
    }

    public List<CategoryStat> getHotCategories()
    {
        return hotCategories;
    }

    public void setHotCategories(List<CategoryStat> hotCategories)
    {
        this.hotCategories = hotCategories;
    }

    public List<ActiveCreator> getActiveCreators()
    {
        return activeCreators;
    }

    public void setActiveCreators(List<ActiveCreator> activeCreators)
    {
        this.activeCreators = activeCreators;
    }

    public static class Summary implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private long productCount;
        private long productOnSaleCount;
        private long orderCount;
        private long paidOrderCount;
        private BigDecimal gmv;
        private long activeCreatorCount;

        public long getProductCount()
        {
            return productCount;
        }

        public void setProductCount(long productCount)
        {
            this.productCount = productCount;
        }

        public long getProductOnSaleCount()
        {
            return productOnSaleCount;
        }

        public void setProductOnSaleCount(long productOnSaleCount)
        {
            this.productOnSaleCount = productOnSaleCount;
        }

        public long getOrderCount()
        {
            return orderCount;
        }

        public void setOrderCount(long orderCount)
        {
            this.orderCount = orderCount;
        }

        public long getPaidOrderCount()
        {
            return paidOrderCount;
        }

        public void setPaidOrderCount(long paidOrderCount)
        {
            this.paidOrderCount = paidOrderCount;
        }

        public BigDecimal getGmv()
        {
            return gmv;
        }

        public void setGmv(BigDecimal gmv)
        {
            this.gmv = gmv;
        }

        public long getActiveCreatorCount()
        {
            return activeCreatorCount;
        }

        public void setActiveCreatorCount(long activeCreatorCount)
        {
            this.activeCreatorCount = activeCreatorCount;
        }
    }

    public static class TrendItem implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String date;
        private long orderCount;
        private BigDecimal gmv;

        public TrendItem()
        {
        }

        public TrendItem(String date, long orderCount, BigDecimal gmv)
        {
            this.date = date;
            this.orderCount = orderCount;
            this.gmv = gmv;
        }

        public String getDate()
        {
            return date;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        public long getOrderCount()
        {
            return orderCount;
        }

        public void setOrderCount(long orderCount)
        {
            this.orderCount = orderCount;
        }

        public BigDecimal getGmv()
        {
            return gmv;
        }

        public void setGmv(BigDecimal gmv)
        {
            this.gmv = gmv;
        }
    }

    public static class CategoryStat implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String categoryName;
        private long orderCount;

        public String getCategoryName()
        {
            return categoryName;
        }

        public void setCategoryName(String categoryName)
        {
            this.categoryName = categoryName;
        }

        public long getOrderCount()
        {
            return orderCount;
        }

        public void setOrderCount(long orderCount)
        {
            this.orderCount = orderCount;
        }
    }

    public static class ActiveCreator implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private String creatorName;
        private long productCount;

        public String getCreatorName()
        {
            return creatorName;
        }

        public void setCreatorName(String creatorName)
        {
            this.creatorName = creatorName;
        }

        public long getProductCount()
        {
            return productCount;
        }

        public void setProductCount(long productCount)
        {
            this.productCount = productCount;
        }
    }
}
