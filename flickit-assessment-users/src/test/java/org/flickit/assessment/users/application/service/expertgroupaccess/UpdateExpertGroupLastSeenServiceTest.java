package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.UpdateExpertGroupLastSeenUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.UpdateExpertGroupLastSeenPort;
import org.flickit.assessment.users.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.test.fixture.application.ExpertGroupAccessMother.activeAccess;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExpertGroupLastSeenServiceTest {

    @InjectMocks
    UpdateExpertGroupLastSeenService service;

    @Mock
    LoadExpertGroupAccessPort loadExpertGroupAccessPort;

    @Mock
    UpdateExpertGroupLastSeenPort updateExpertGroupLastSeenPort;

    @Test
    @DisplayName("When the current user is a member of the expertGroup, the update LastSeen should function properly.")
    void testUpdateExpertGroupLastSeenService_isMember_success() {
        UUID currentUserId = UUID.randomUUID();
        ExpertGroupAccess expertGroupAccess = activeAccess();
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup("path", currentUserId);
        UpdateExpertGroupLastSeenUseCase.Param param = new UpdateExpertGroupLastSeenUseCase.Param(expertGroup.getId(), currentUserId);

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroup.getId(), currentUserId)).thenReturn(Optional.of(expertGroupAccess));
        doNothing().when(updateExpertGroupLastSeenPort).updateLastSeen(anyLong(), any(UUID.class), any(LocalDateTime.class));

        assertDoesNotThrow(()-> service.updateLastSeen(param));

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(expertGroup.getId(), expertGroup.getOwnerId());
        verify(updateExpertGroupLastSeenPort).updateLastSeen(anyLong(), any(UUID.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("When the current user is not a member of the expertGroup, the last-seen update cannot be performed.")
    void testUpdateExpertGroupLastSeenService_isNotMember_accessDenied() {
        UUID ownerId = UUID.randomUUID();
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup("Path", ownerId);
        UUID currentUserId = UUID.randomUUID();
        UpdateExpertGroupLastSeenUseCase.Param param = new UpdateExpertGroupLastSeenUseCase.Param(expertGroup.getId(), currentUserId);

        when(loadExpertGroupAccessPort.loadExpertGroupAccess(expertGroup.getId(), currentUserId)).thenReturn(Optional.empty());

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.updateLastSeen(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadExpertGroupAccessPort).loadExpertGroupAccess(expertGroup.getId(), param.getCurrentUserId());
    }
}
