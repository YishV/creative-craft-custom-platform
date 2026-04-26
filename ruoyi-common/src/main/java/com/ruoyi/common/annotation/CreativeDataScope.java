package com.ruoyi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文创业务数据权限过滤注解
 * <p>
 * 贴在 Service 列表查询方法上，由切面在管理员之外的角色上自动注入归属字段，
 * 替代各 Service 内部重复的 isAdmin / setUserId / setCreatorId 模板。
 *
 * <ul>
 *   <li>{@link Owner#BUYER}：注入当前登录用户的 userId 到查询参数；</li>
 *   <li>{@link Owner#CREATOR}：注入当前登录用户对应的 creatorId；若无生效创作者身份，
 *       切面短路返回空列表，不再触达 mapper。</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreativeDataScope
{
    /** 归属语义。 */
    Owner owner();

    /** 注入到查询参数 (方法第一个参数) 上的字段名。 */
    String field();

    enum Owner
    {
        BUYER,
        CREATOR
    }
}
