package org.flickit.assessment.users.application.service.space;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpaceAssessmentPort;
import org.flickit.assessment.users.application.port.out.space.DeleteSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_ASSESSMENT_EXIST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceServiceTest {

    @InjectMocks
    DeleteSpaceService service;

    @Mock
    LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    CountSpaceAssessmentPort countSpaceAssessmentPort;

    @Mock
    DeleteSpacePort deleteSpacePort;

    @Test
    void testDeleteSpase_whenCurrentUserIsNotOwner_thenThrowAccessDeniedException() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteSpace(param));
        Assertions.assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    @Test
    void testDeleteSpase_whenAssessmentsCountIsNotZero_thenThrowResourceNotFound() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        when(countSpaceAssessmentPort.countAssessments(spaceId)).thenReturn(1);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteSpace(param));
        assertEquals(DELETE_SPACE_ASSESSMENT_EXIST, throwable.getMessageKey());
    }

    @Test
    void testDeleteSpase_whenParametersAreValid_thenSuccessfulDelete() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        DeleteSpaceUseCase.Param param = new DeleteSpaceUseCase.Param(spaceId, currentUserId);

        when(loadSpaceOwnerPort.loadOwnerId(spaceId)).thenReturn(currentUserId);
        when(countSpaceAssessmentPort.countAssessments(spaceId)).thenReturn(0);
        doNothing().when(deleteSpacePort).deleteById(anyLong(), anyLong());

        assertDoesNotThrow(() -> service.deleteSpace(param));
    }
}
