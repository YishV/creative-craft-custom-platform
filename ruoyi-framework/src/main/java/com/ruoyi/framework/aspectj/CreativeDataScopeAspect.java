package com.ruoyi.framework.aspectj;

import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.Collections;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文创业务数据权限切面。
 * <p>
 * 拦截所有标注 {@link CreativeDataScope} 的方法：管理员透传；
 * 买家/创作者根据声明把归属字段注入到查询参数；创作者无生效身份时直接短路返回空列表。
 */
@Aspect
@Component
public class CreativeDataScopeAspect
{
    @Autowired
    private CreativeDataPermissionService permissionService;

    @Around("@annotation(scope)")
    public Object around(ProceedingJoinPoint pjp, CreativeDataScope scope) throws Throwable
    {
        Object[] args = pjp.getArgs();
        Object query = args != null && args.length > 0 ? args[0] : null;
        boolean shouldProceed = permissionService.applyListScope(query, scope.owner(), scope.field());
        if (!shouldProceed)
        {
            return Collections.emptyList();
        }
        return pjp.proceed(args);
    }
}
