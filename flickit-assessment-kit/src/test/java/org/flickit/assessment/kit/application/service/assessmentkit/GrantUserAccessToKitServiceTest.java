package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GrantUserAccessToKitUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother.createExpertGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrantUserAccessToKitServiceTest {

    @InjectMocks
    private GrantUserAccessToKitService service;

    @Mock
    private LoadKitExpertGroupPort loadExpertGroupIdPort;

    @Mock
    private GrantUserAccessToKitPort grantUserAccessToKitPort;

    @Test
    void testGrantUserAccessToKit_ValidParams_AddUserSuccessfully() {
        ExpertGroup expertGroup = createExpertGroup();

        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            expertGroup.getOwnerId()
        );
        when(loadExpertGroupIdPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        doNothing().when(grantUserAccessToKitPort).grantUserAccess(param.getKitId(), param.getUserId());

        service.grantUserAccessToKit(param);

        var LoadExpertGroupIdParam = ArgumentCaptor.forClass(Long.class);
        verify(loadExpertGroupIdPort, times(1)).loadKitExpertGroup(LoadExpertGroupIdParam.capture());
        assertEquals(param.getKitId(), LoadExpertGroupIdParam.getValue());

        var grantAccessKitIdParam = ArgumentCaptor.forClass(Long.class);
        var grantAccessUserIdParam = ArgumentCaptor.forClass(UUID.class);
        verify(grantUserAccessToKitPort, times(1))
            .grantUserAccess(grantAccessKitIdParam.capture(), grantAccessUserIdParam.capture());
        assertEquals(param.getKitId(), grantAccessKitIdParam.getValue());
        assertEquals(param.getUserId(), grantAccessUserIdParam.getValue());
    }

    @Test
    void testGrantUserAccessToKit_InvalidCurrentUser_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        ExpertGroup expertGroup = createExpertGroup();

        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            currentUserId
        );

        when(loadExpertGroupIdPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }

    @Test
    void testGrantUserAccessToKit_InvalidKitIdExpertGroupOwnerNull_ThrowsException() {
        var currentUserId = UUID.randomUUID();
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();

        GrantUserAccessToKitUseCase.Param param = new GrantUserAccessToKitUseCase.Param(
            1L,
            UUID.randomUUID(),
            currentUserId
        );
        when(loadExpertGroupIdPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);

        var exception = assertThrows(AccessDeniedException.class, () -> service.grantUserAccessToKit(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
        verify(loadExpertGroupIdPort, times(1)).loadKitExpertGroup(any());
        verify(grantUserAccessToKitPort, never()).grantUserAccess(any(), any());
    }
}
