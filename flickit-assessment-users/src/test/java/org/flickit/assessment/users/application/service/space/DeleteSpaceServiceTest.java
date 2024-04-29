package org.flickit.assessment.users.application.service.space;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistsPort;
import org.flickit.assessment.users.application.port.out.space.CountSpaceAssessmentPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_ASSESSMENT_EXIST;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceServiceTest {
    @InjectMocks
    DeleteSpaceService service;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;
    @Mock
    CheckSpaceExistsPort checkSpaceExistsPort;
    @Mock
    CountSpaceAssessmentPort countSpaceAssessmentPort;
    //@Mock
//    DeleteSpacePort deleteSpacePort;
//

//    @Mock
//    CountSpaceAssessmentPort countSpaceAssessmentPort;
    @Test
    @DisplayName("Deleting a space that the owner is null causes ResourceNotFoundException")
    void testDeleteSpase_ownerIsNull_resourceNotFound() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenThrow(new ResourceNotFoundException(SPACE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteSpace(param));
        Assertions.assertThat(throwable).hasMessage(SPACE_ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deleting a space should be accomplished by owner")
    void testDeleteSpase_currentUserIsNotOwner_accessDeniedException() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteSpace(param));
        Assertions.assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    @DisplayName("Deleting a space should be accomplished on an existed space")
    void testDeleteSpase_spaceIdNotFound_resourceNotFound() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        when(checkSpaceExistsPort.existsById(spaceId)).thenReturn(false);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteSpace(param));
        Assertions.assertThat(throwable).hasMessage(SPACE_ID_NOT_FOUND);
    }

    @Test
    @DisplayName("Deleting a space should be accomplished on a space with no assessments.")
    void testDeleteSpase_assessmentsCountIsNotZero_resourceNotFound() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        when(checkSpaceExistsPort.existsById(spaceId)).thenReturn(true);
        when(countSpaceAssessmentPort.countAssessments(spaceId)).thenReturn(1);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteSpace(param));
        Assertions.assertThat(throwable).hasMessage(DELETE_SPACE_ASSESSMENT_EXIST);
    }

}
