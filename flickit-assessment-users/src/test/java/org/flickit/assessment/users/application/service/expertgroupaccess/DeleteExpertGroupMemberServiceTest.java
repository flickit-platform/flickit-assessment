package org.flickit.assessment.users.application.service.expertgroupaccess;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.DeleteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.DeleteExpertGroupMemberPort;
import org.flickit.assessment.users.test.fixture.application.ExpertGroupMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_EXPERT_GROUP_MEMBER_USER_ID_OWNER_DELETION_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpertGroupMemberServiceTest {

    @InjectMocks
    private DeleteExpertGroupMemberService service;

    @Mock
    private LoadExpertGroupPort loadExpertGroupPort;

    @Mock
    private DeleteExpertGroupMemberPort deleteExpertGroupMemberPort;

    @Test
    @DisplayName("Delete 'Expert Group Member' with valid parameters should cause a successful deletion")
    void deleteMember_validParameter_successful() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup("picturePath", currentUserId);

        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        doNothing().when(deleteExpertGroupMemberPort).deleteMember(expertGroupId, userId);

        assertDoesNotThrow(() -> service.deleteMember(param));

        verify(loadExpertGroupPort, times(1)).loadExpertGroup(expertGroupId);
        verify(deleteExpertGroupMemberPort, times(1)).deleteMember(expertGroupId, userId);
    }

    @Test
    @DisplayName("Delete 'Expert Group Member' when user is not a member should cause a ResourceNotFoundException")
    void deleteMember_userNotMember_ResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup("picturePath", currentUserId);

        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);
        doThrow(new ResourceNotFoundException(""))
            .when(deleteExpertGroupMemberPort).deleteMember(expertGroupId, userId);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteMember(param));
        verify(loadExpertGroupPort, times(1)).loadExpertGroup(expertGroupId);
        verify(deleteExpertGroupMemberPort, times(1)).deleteMember(expertGroupId, userId);
    }

    @Test
    @DisplayName("Delete 'Expert Group Member' with invalid owner should cause a AccessDeniedException")
    void deleteMember_currentUserNotOwner_AccessDeniedException() {
        UUID userId = UUID.randomUUID();
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        UUID expertGroupOwnerId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, userId, currentUserId);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup("picturePath", expertGroupOwnerId);

        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertThrows(AccessDeniedException.class, () -> service.deleteMember(param));
        verify(loadExpertGroupPort, times(1)).loadExpertGroup(expertGroupId);
        verifyNoInteractions(deleteExpertGroupMemberPort);
    }

    @Test
    @DisplayName("Delete 'Expert Group Owner' from expert group members should cause a ValidationException")
    void deleteMember_userIsOwner_ValidationException() {
        long expertGroupId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteExpertGroupMemberUseCase.Param param =
            new DeleteExpertGroupMemberUseCase.Param(expertGroupId, currentUserId, currentUserId);
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup("picturePath", currentUserId);

        when(loadExpertGroupPort.loadExpertGroup(expertGroupId)).thenReturn(expertGroup);

        assertThrows(ValidationException.class, () -> service.deleteMember(param),
            DELETE_EXPERT_GROUP_MEMBER_USER_ID_OWNER_DELETION_NOT_ALLOWED);
        verify(loadExpertGroupPort, times(1)).loadExpertGroup(expertGroupId);
        verifyNoInteractions(deleteExpertGroupMemberPort);
    }
}
