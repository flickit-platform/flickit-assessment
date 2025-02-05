package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAssessmentAttributesUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_ATTRIBUTES;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAttributesServiceTest {

    @InjectMocks
    private GetAssessmentAttributesService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Captor
    private ArgumentCaptor<LoadAttributesPort.Result> loadAttributesPortResultCaptor;

    @Test
    void testGetAssessmentAttributesService_whenCurrentUserDoesNotHaveAccess_thenThrowsAccessDeniedException() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAttributes(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }


    @Test
    void testGetAssessmentAttributesService_whenNoAttributesExist_thenReturnEmptyResult() {
        var param = createParam(GetAssessmentAttributesUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_ATTRIBUTES))
            .thenReturn(true);
        when(loadAttributesPort.loadAttributes(param.getAssessmentId()))
            .thenReturn(List.of());

        var result = service.getAssessmentAttributes(param);
        assertNotNull(result);
        assertNull(result.attributes());
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
