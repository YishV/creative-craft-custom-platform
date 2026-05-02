package com.ruoyi.system.domain.creative;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

public class CreativeDemand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long demandId;
    private Long userId;
    private String userName;
    private Long categoryId;
    private String demandTitle;
    private BigDecimal budgetAmount;
    private String demandStatus;

    public Long getDemandId()
    {
        return demandId;
    }

    public void setDemandId(Long demandId)
    {
        this.demandId = demandId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getDemandTitle()
    {
        return demandTitle;
    }

    public void setDemandTitle(String demandTitle)
    {
        this.demandTitle = demandTitle;
    }

    public BigDecimal getBudgetAmount()
    {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount)
    {
        this.budgetAmount = budgetAmount;
    }

    public String getDemandStatus()
    {
        return demandStatus;
    }

    public void setDemandStatus(String demandStatus)
    {
        this.demandStatus = demandStatus;
    }
}
