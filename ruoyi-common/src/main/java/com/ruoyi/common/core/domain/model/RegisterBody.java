package com.ruoyi.common.core.domain.model;

/**
 * 用户注册对象
 *
 * @author ruoyi
 */
public class RegisterBody extends LoginBody
{
    /** 注册身份：buyer / creator，由前台登录入口决定，影响默认部门归属 */
    private String identityType;

    public String getIdentityType()
    {
        return identityType;
    }

    public void setIdentityType(String identityType)
    {
        this.identityType = identityType;
    }
}
