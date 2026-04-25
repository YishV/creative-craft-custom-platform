package com.ruoyi.system.service.creative.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeDataPermissionServiceTest
{
    @Mock
    private CreativeCreatorMapper creativeCreatorMapper;

    @Spy
    @InjectMocks
    private CreativeDataPermissionService permissionService;

    @Test
    void requireCurrentCreatorIdShouldReturnCreatorIdWhenCreatorIsApprovedAndNormal()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));

        Long creatorId = permissionService.requireCurrentCreatorId();

        assertEquals(88L, creatorId);
    }

    @Test
    void getCurrentCreatorIdOrNullShouldReturnNullWhenNoEffectiveCreatorExists()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(Collections.singletonList(buildCreator(88L, 7L, "1", "approved")));

        Long creatorId = permissionService.getCurrentCreatorIdOrNull();

        assertNull(creatorId);
    }

    @Test
    void requireCurrentCreatorIdShouldFailWhenNoEffectiveCreatorExists()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(Collections.emptyList());

        assertThrows(ServiceException.class, () -> permissionService.requireCurrentCreatorId());
    }

    private CreativeCreator buildCreator(Long creatorId, Long userId, String status, String auditStatus)
    {
        CreativeCreator creator = new CreativeCreator();
        creator.setCreatorId(creatorId);
        creator.setUserId(userId);
        creator.setStatus(status);
        creator.setAuditStatus(auditStatus);
        return creator;
    }
}
