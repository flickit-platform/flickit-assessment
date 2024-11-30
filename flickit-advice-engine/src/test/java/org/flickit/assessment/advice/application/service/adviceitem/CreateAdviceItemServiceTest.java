package org.flickit.assessment.advice.application.service.adviceitem;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.in.adviceitem.CreateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
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

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdviceItemServiceTest {

    @InjectMocks
    CreateAdviceItemService service;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    CreateAdviceItemPort createAdviceItemPort;

    @Test
    void testCreateAdviceItem_whenUserIsNotAuthorized_thenThrowAccessDeniedException() {
        var param = createParam(CreateAdviceItemUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAdviceItem(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, validateAssessmentResultPort, createAdviceItemPort);
    }

    @Test
    void testCreateAdviceItem_whenAssessmentResultNotExist_thenThrowResourceException() {
        var param = createParam(CreateAdviceItemUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAdviceItem(param));
        assertEquals(CREATE_ADVICE_ITEM_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort, createAdviceItemPort);
    }

    @Test
    void testCreateAdviceItem_whenValidParameter_thenCreatesAdviceItemSuccessfully() {
        var param = createParam(CreateAdviceItemUseCase.Param.ParamBuilder::build);
        var assessmentResult = new AssessmentResult(UUID.randomUUID(), 1L);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));

        service.createAdviceItem(param);

        ArgumentCaptor<AdviceItem> argumentCaptor = ArgumentCaptor.forClass(AdviceItem.class);
        verify(createAdviceItemPort).persist(argumentCaptor.capture());
        assertEquals(param.getTitle(), argumentCaptor.getValue().getTitle());
        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().getAssessmentResultId());
        assertEquals(param.getDescription(), argumentCaptor.getValue().getDescription());
        assertEquals(param.getCost(), argumentCaptor.getValue().getCost().getCode());
        assertEquals(param.getPriority(), argumentCaptor.getValue().getPriority().getCode());
        assertEquals(param.getImpact(), argumentCaptor.getValue().getImpact().getCode());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().getCreatedBy());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().getLastModifiedBy());

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
    }

    private CreateAdviceItemUseCase.Param createParam(Consumer<CreateAdviceItemUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAdviceItemUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAdviceItemUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .title("title")
            .description("description")
            .cost("LOW")
            .impact("MEDIUM")
            .priority("HIGH")
            .currentUserId(UUID.randomUUID());
    }
}
