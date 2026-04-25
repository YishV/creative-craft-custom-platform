package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserPostMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest
{
    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysPostMapper postMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private SysUserPostMapper userPostMapper;

    @Mock
    private ISysConfigService configService;

    @Mock
    private ISysDeptService deptService;

    @Mock
    private Validator validator;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @Test
    @SuppressWarnings("unchecked")
    void registerUserShouldAppendBuyerRoleAfterInsert()
    {
        SysUser user = buildUser();
        when(userMapper.insertUser(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser inserted = invocation.getArgument(0);
            inserted.setUserId(100L);
            return 1;
        });
        when(roleMapper.checkRoleKeyUnique("buyer")).thenReturn(buildRole(3L, "buyer"));
        when(roleMapper.selectRoleListByUserId(100L)).thenReturn(Collections.emptyList());

        boolean result = sysUserService.registerUser(user);

        assertTrue(result);
        ArgumentCaptor<List<SysUserRole>> captor = ArgumentCaptor.forClass(List.class);
        verify(userRoleMapper).batchUserRole(captor.capture());
        assertEquals(100L, captor.getValue().get(0).getUserId());
        assertEquals(3L, captor.getValue().get(0).getRoleId());
    }

    @Test
    void appendRoleByKeyIfAbsentShouldSkipWhenRoleAlreadyAssigned()
    {
        when(roleMapper.checkRoleKeyUnique("buyer")).thenReturn(buildRole(3L, "buyer"));
        when(roleMapper.selectRoleListByUserId(100L)).thenReturn(List.of(3L));

        sysUserService.appendRoleByKeyIfAbsent(100L, "buyer");

        verify(userRoleMapper, never()).batchUserRole(any());
    }

    @Test
    void appendRoleByKeyIfAbsentShouldFailWhenRoleMissing()
    {
        when(roleMapper.checkRoleKeyUnique("buyer")).thenReturn(null);

        assertThrows(ServiceException.class, () -> sysUserService.appendRoleByKeyIfAbsent(100L, "buyer"));

        verify(userRoleMapper, never()).batchUserRole(any());
    }

    private SysUser buildUser()
    {
        SysUser user = new SysUser();
        user.setUserName("buyer01");
        user.setNickName("buyer01");
        user.setPassword("encoded-password");
        return user;
    }

    private SysRole buildRole(Long roleId, String roleKey)
    {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setRoleKey(roleKey);
        role.setRoleName(roleKey);
        return role;
    }
}
