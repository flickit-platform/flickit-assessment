package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_ATTRIBUTES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributesServiceTest {

    @InjectMocks
    private GetAssessmentAttributesService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAssessmentAttributesService_whenCurrentUserDoesNotHaveAccess_thenThrowsAccessDeniedException() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAttributes(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private GetAssessmentAttributesUseCase.Param createParam(Consumer<GetAssessmentAttributesUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentAttributesUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentAttributesUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
