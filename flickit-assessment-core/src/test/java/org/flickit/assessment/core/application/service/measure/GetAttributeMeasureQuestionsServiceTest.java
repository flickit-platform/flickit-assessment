package org.flickit.assessment.core.application.service.measure;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_MEASURE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeMeasureQuestionsServiceTest {

    @InjectMocks
    private GetAttributeMeasureQuestionsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    private final GetAttributeMeasureQuestionsUseCase.Param param =
        createParam(GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder::build);

    @Test
    void testGetAttributeMeasureQuestions_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_MEASURE_QUESTIONS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAttributeMeasureQuestions(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private GetAttributeMeasureQuestionsUseCase.Param createParam(Consumer<GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeMeasureQuestionsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .measureId(3L)
            .currentUserId(UUID.randomUUID());
    }
}
