package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.space.DeleteSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpaceAssessmentPort;
import org.flickit.assessment.users.application.port.out.space.DeleteSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_ASSESSMENT_EXIST;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_SPACE_DEFAULT_SPACE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSpaceServiceTest {

    @InjectMocks
    private DeleteSpaceService service;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Mock
    private CountSpaceAssessmentPort countSpaceAssessmentPort;

    @Mock
    private DeleteSpacePort deleteSpacePort;

    private final DeleteSpaceUseCase.Param param = createParam(DeleteSpaceUseCase.Param.ParamBuilder::build);

    @Test
    void testDeleteSpase_whenCurrentUserIsNotOwner_thenThrowAccessDeniedException() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteSpace(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadSpacePort, countSpaceAssessmentPort, deleteSpacePort);
    }

    @Test
    void testDeleteSpase_whenSpaceIsDefault_thenThrowValidationException() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(param.getCurrentUserId());
        when(loadSpacePort.checkIsDefault(param.getId())).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, ()-> service.deleteSpace(param));
        assertEquals(DELETE_SPACE_SPACE_DEFAULT_SPACE, throwable.getMessageKey());

        verifyNoInteractions(countSpaceAssessmentPort, deleteSpacePort);
    }

    @Test
    void testDeleteSpase_whenAssessmentsCountIsNotZero_thenThrowResourceNotFound() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(param.getCurrentUserId());
        when(loadSpacePort.checkIsDefault(param.getId())).thenReturn(false);
        when(countSpaceAssessmentPort.countAssessments(param.getId())).thenReturn(1);

        var throwable = assertThrows(ValidationException.class, () -> service.deleteSpace(param));
        assertEquals(DELETE_SPACE_ASSESSMENT_EXIST, throwable.getMessageKey());

        verifyNoInteractions(deleteSpacePort);
    }

    @Test
    void testDeleteSpase_whenParametersAreValid_thenSuccessfulDelete() {
        when(loadSpacePort.loadOwnerId(param.getId())).thenReturn(param.getCurrentUserId());
        when(countSpaceAssessmentPort.countAssessments(param.getId())).thenReturn(0);
        when(loadSpacePort.checkIsDefault(param.getId())).thenReturn(false);
        when(countSpaceAssessmentPort.countAssessments(param.getId())).thenReturn(0);

        service.deleteSpace(param);
        verify(deleteSpacePort).deleteById(eq(param.getId()), anyLong());
    }

    private DeleteSpaceUseCase.Param createParam(Consumer<DeleteSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteSpaceUseCase.Param.builder()
            .id(0L)
            .currentUserId(UUID.randomUUID());
    }
}
