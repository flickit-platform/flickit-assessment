package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.subject.GetSubjectInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSubjectInsightServiceTest {

    @InjectMocks
    private GetSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private GetSubjectInsightHelper getSubjectInsightHelper;

    @Test
    void testGetSubjectInsight_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var exception = assertThrows(AccessDeniedException.class, () -> service.getSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            getSubjectInsightHelper);
    }

    @Test
    void testGetSubjectInsight_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getSubjectInsight(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort, getSubjectInsightHelper);
    }

    @Test
    void testGetSubjectInsight_whenHelperReturnsInsight_thenReturnsSameInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var insight = new Insight(null, new Insight.InsightDetail("insight", LocalDateTime.now(), true), true, true);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(getSubjectInsightHelper.getSubjectInsight(assessmentResult, param.getSubjectId(), param.getCurrentUserId()))
            .thenReturn(insight);

        var result = service.getSubjectInsight(param);

        assertEquals(insight, result);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
