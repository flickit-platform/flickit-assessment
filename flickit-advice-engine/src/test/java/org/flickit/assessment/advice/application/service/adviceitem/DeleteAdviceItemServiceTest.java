package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.port.in.adviceitem.DeleteAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAdviceItemServiceTest {

    @InjectMocks
    DeleteAdviceItemService service;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    DeleteAdviceItemPort deleteAdviceItemPort;

    @Test
    void testDeleteAdviceItem_whenUserIsNotAuthorized_thenThrowAccessDeniedException() {
        var param = createParam(DeleteAdviceItemUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteAdviceItem(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteAdviceItemPort);
    }

    @Test
    void testDeleteAdviceItem_whenParametersAreValid_thenThrowAccessDeniedException() {
        var param = createParam(DeleteAdviceItemUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);

        service.deleteAdviceItem(param);

        verify(deleteAdviceItemPort).deleteAdviceItem(param.getAdviceItemId());
    }

    private DeleteAdviceItemUseCase.Param createParam(Consumer<DeleteAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}
