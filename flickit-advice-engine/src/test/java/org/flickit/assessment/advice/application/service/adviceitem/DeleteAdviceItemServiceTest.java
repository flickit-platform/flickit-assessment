package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.advice.common.ErrorMessageKey.DELETE_ADVICE_ITEM_ASSESSMENT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ADVICE_ITEM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAdviceItemServiceTest {

    @InjectMocks
    DeleteAdviceItemService service;

    @Mock
    LoadAdviceItemPort loadAdviceItemPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    DeleteAdviceItemPort deleteAdviceItemPort;

    @Test
    void testDeleteAdviceItem_whenAssessmentNotExists_thenThrowResourceNotFoundException() {
        var param = createParam(DeleteAdviceItemUseCase.Param.ParamBuilder::build);

        when(loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.deleteAdviceItem(param));
        assertEquals(DELETE_ADVICE_ITEM_ASSESSMENT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(assessmentAccessChecker, deleteAdviceItemPort);
    }

    @Test
    void testDeleteAdviceItem_whenUserNotAuthorized_thenThrowAccessDeniedException() {
        var param = createParam(DeleteAdviceItemUseCase.Param.ParamBuilder::build);
        var assessmentId = UUID.randomUUID();

        when(loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())).thenReturn(Optional.of(assessmentId));
        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), MANAGE_ADVICE_ITEM)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteAdviceItem(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteAdviceItemPort);
    }

    @Test
    void testDeleteAdviceItem_whenValidParams_thenSuccess() {
        var param = createParam(DeleteAdviceItemUseCase.Param.ParamBuilder::build);
        var assessmentId = UUID.randomUUID();

        when(loadAdviceItemPort.loadAssessmentIdById(param.getAdviceItemId())).thenReturn(Optional.of(assessmentId));
        when(assessmentAccessChecker.isAuthorized(assessmentId, param.getCurrentUserId(), MANAGE_ADVICE_ITEM)).thenReturn(true);

        service.deleteAdviceItem(param);

        verify(deleteAdviceItemPort).delete(param.getAdviceItemId());
    }

    private DeleteAdviceItemUseCase.Param createParam(Consumer<DeleteAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
