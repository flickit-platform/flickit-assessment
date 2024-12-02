package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.in.adviceitem.UpdateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.advice.common.ErrorMessageKey.UPDATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAdviceItemServiceTest {

    @InjectMocks
    UpdateAdviceItemService service;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    UpdateAdviceItemPort updateAdviceItemPort;

    @Test
    void testUpdateAdviceItem_whenUserIsNotAuthorized_thenThrowAccessDeniedException() {
        var param = createParam(UpdateAdviceItemUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAdviceItem(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, validateAssessmentResultPort, updateAdviceItemPort);
    }

    @Test
    void testUpdateAdviceItem_whenAssessmentResultNotExist_thenThrowResourceException() {
        var param = createParam(UpdateAdviceItemUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateAdviceItem(param));
        assertEquals(UPDATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort, updateAdviceItemPort);
    }

    @Test
    void testUpdateAdviceItem_whenValidParameter_thenUpdatesAdviceItemSuccessfully() {
        var param = createParam(UpdateAdviceItemUseCase.Param.ParamBuilder::build);
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 1L);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));

        service.updateAdviceItem(param);

        ArgumentCaptor<AdviceItem> argumentCaptor = ArgumentCaptor.forClass(AdviceItem.class);
        verify(updateAdviceItemPort).updateAdviceItem(argumentCaptor.capture());
        assertEquals(param.getTitle(), argumentCaptor.getValue().getTitle());
        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().getAssessmentResultId());
        assertEquals(param.getDescription(), argumentCaptor.getValue().getDescription());
        assertEquals(param.getCost(), argumentCaptor.getValue().getCost().name());
        assertEquals(param.getImpact(), argumentCaptor.getValue().getImpact().name());
        assertEquals(param.getPriority(), argumentCaptor.getValue().getPriority().name());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().getCreatedBy());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().getLastModifiedBy());

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
    }


    private UpdateAdviceItemUseCase.Param createParam(Consumer<UpdateAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAdviceItemUseCase.Param.builder()
            .adviceItemId(UUID.randomUUID())
            .assessmentId(UUID.randomUUID())
            .title("title")
            .description("description")
            .cost("LOW")
            .impact("MEDIUM")
            .priority("HIGH")
            .currentUserId(UUID.randomUUID());
    }
}