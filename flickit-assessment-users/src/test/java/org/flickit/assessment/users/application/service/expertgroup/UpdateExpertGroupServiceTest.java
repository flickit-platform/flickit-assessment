package org.flickit.assessment.users.application.service.expertgroup;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroup.UpdateExpertGroupPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class UpdateExpertGroupServiceTest {

    @InjectMocks
    UpdateExpertGroupService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    UpdateExpertGroupPort updateExpertGroupPort;

    @Test
    @DisplayName("If the currentUser is not owner, service should throw AccessDenied")
    void testUpdateExpertGroup_currentUserIsNotOwner_AccessDeniedException(){
        var expertGroupId = 1L;
        var currentUserId = UUID.randomUUID();
        UpdateExpertGroupUseCase.Param param = new UpdateExpertGroupUseCase.Param(expertGroupId, "title", "bio",
            "about", "https://www.google.com", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(UUID.randomUUID());

        Throwable throwable = assertThrows(AccessDeniedException.class, ()-> service.updateExpertGroup(param));
        Assertions.assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(updateExpertGroupPort);
    }

    @Test
    @DisplayName("If the expert group does not exist, service should throw ResourceNotFound")
    void testUpdateExpertGroup_expertGroupNotExists_ResourceNotFoundException(){
        var expertGroupId = 1L;
        var currentUserId = UUID.randomUUID();
        UpdateExpertGroupUseCase.Param param = new UpdateExpertGroupUseCase.Param(expertGroupId, "title", "bio",
            "about", "https://www.google.com", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenThrow(new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        Throwable throwable = assertThrows(ResourceNotFoundException.class, ()-> service.updateExpertGroup(param));
        Assertions.assertThat(throwable).hasMessage(EXPERT_GROUP_ID_NOT_FOUND);

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verifyNoInteractions(updateExpertGroupPort);
    }

    @Test
    @DisplayName("With valid parameters, the service should cause successful update")
    void testUpdateExpertGroup_validParameters_successful(){
        var expertGroupId = 1L;
        var currentUserId = UUID.randomUUID();
        UpdateExpertGroupUseCase.Param param = new UpdateExpertGroupUseCase.Param(expertGroupId, "title", "bio",
            "about", "https://www.google.com", currentUserId);

        when(loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)).thenReturn(currentUserId);
        doNothing().when(updateExpertGroupPort).update(any());

        assertDoesNotThrow(()-> service.updateExpertGroup(param));

        verify(loadExpertGroupOwnerPort).loadOwnerId(expertGroupId);
        verify(updateExpertGroupPort).update(any());
    }
}
