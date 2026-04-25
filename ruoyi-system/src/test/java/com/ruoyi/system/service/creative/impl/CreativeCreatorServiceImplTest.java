package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.service.ISysUserService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeCreatorServiceImplTest
{
    @Mock
    private CreativeCreatorMapper creativeCreatorMapper;

    @Mock
    private ISysUserService sysUserService;

    @InjectMocks
    private CreativeCreatorServiceImpl creativeCreatorService;

    @Test
    void applyCreatorShouldCreatePendingApplication()
    {
        CreativeCreator creator = buildCreator();
        when(sysUserService.selectUserById(100L)).thenReturn(buildUser(100L));
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class))).thenReturn(Collections.emptyList());
        when(creativeCreatorMapper.insertCreativeCreator(any(CreativeCreator.class))).thenReturn(1);

        int rows = creativeCreatorService.applyCreator(creator);

        assertEquals(1, rows);
        ArgumentCaptor<CreativeCreator> captor = ArgumentCaptor.forClass(CreativeCreator.class);
        verify(creativeCreatorMapper).insertCreativeCreator(captor.capture());
        assertEquals("pending", captor.getValue().getAuditStatus());
        assertEquals("1", captor.getValue().getStatus());
        assertEquals("codex", captor.getValue().getCreateBy());
    }

    @Test
    void applyCreatorShouldFailWhenPendingOrApprovedApplicationExists()
    {
        CreativeCreator creator = buildCreator();
        when(sysUserService.selectUserById(100L)).thenReturn(buildUser(100L));
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(existingCreator("pending")));

        assertThrows(ServiceException.class, () -> creativeCreatorService.applyCreator(creator));

        verify(creativeCreatorMapper, never()).insertCreativeCreator(any(CreativeCreator.class));
    }

    @Test
    void approveCreatorShouldAppendCreatorRole()
    {
        CreativeCreator creator = existingCreator("pending");
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(creator);
        when(sysUserService.selectUserById(100L)).thenReturn(buildUser(100L));
        when(creativeCreatorMapper.updateCreativeCreator(any(CreativeCreator.class))).thenReturn(1);

        int rows = creativeCreatorService.approveCreator(1L, "admin");

        assertEquals(1, rows);
        ArgumentCaptor<CreativeCreator> creatorCaptor = ArgumentCaptor.forClass(CreativeCreator.class);
        verify(creativeCreatorMapper).updateCreativeCreator(creatorCaptor.capture());
        assertEquals("approved", creatorCaptor.getValue().getAuditStatus());
        assertEquals("0", creatorCaptor.getValue().getStatus());
        assertEquals("admin", creatorCaptor.getValue().getAuditBy());
        verify(sysUserService).appendRoleByKeyIfAbsent(100L, "creator");
    }

    @Test
    void approveCreatorShouldFailWhenApplicationAlreadyReviewed()
    {
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(existingCreator("approved"));

        assertThrows(ServiceException.class, () -> creativeCreatorService.approveCreator(1L, "admin"));

        verify(creativeCreatorMapper, never()).updateCreativeCreator(any(CreativeCreator.class));
        verify(sysUserService, never()).appendRoleByKeyIfAbsent(any(), any());
    }

    @Test
    void rejectCreatorShouldStoreRejectReason()
    {
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(existingCreator("pending"));
        when(creativeCreatorMapper.updateCreativeCreator(any(CreativeCreator.class))).thenReturn(1);

        int rows = creativeCreatorService.rejectCreator(1L, "资料不完整", "admin");

        assertEquals(1, rows);
        ArgumentCaptor<CreativeCreator> creatorCaptor = ArgumentCaptor.forClass(CreativeCreator.class);
        verify(creativeCreatorMapper).updateCreativeCreator(creatorCaptor.capture());
        assertEquals("rejected", creatorCaptor.getValue().getAuditStatus());
        assertEquals("资料不完整", creatorCaptor.getValue().getAuditRemark());
        assertEquals("1", creatorCaptor.getValue().getStatus());
    }

    @Test
    void rejectCreatorShouldFailWhenReasonIsBlank()
    {
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(existingCreator("pending"));

        assertThrows(ServiceException.class, () -> creativeCreatorService.rejectCreator(1L, "  ", "admin"));

        verify(creativeCreatorMapper, never()).updateCreativeCreator(any(CreativeCreator.class));
    }

    private CreativeCreator buildCreator()
    {
        CreativeCreator creator = new CreativeCreator();
        creator.setUserId(100L);
        creator.setCreatorName("手作爱好者");
        creator.setStoreName("纸间工坊");
        creator.setCreatorLevel("newbie");
        creator.setCreateBy("codex");
        return creator;
    }

    private CreativeCreator existingCreator(String auditStatus)
    {
        CreativeCreator creator = buildCreator();
        creator.setCreatorId(1L);
        creator.setStatus("1");
        creator.setAuditStatus(auditStatus);
        return creator;
    }

    private SysUser buildUser(Long userId)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName("user" + userId);
        return user;
    }
}
