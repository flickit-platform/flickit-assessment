package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.insight.ApproveAllAssessmentInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.ApproveAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.ApproveAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.ApproveSubjectInsightPort;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithAssessmentResultId;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithTimesAndApprove;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveAllAssessmentInsightsServiceTest {

    @InjectMocks
    private ApproveAllAssessmentInsightsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private ApproveAssessmentInsightPort approveAssessmentInsightPort;

    @Mock
    private ApproveSubjectInsightPort approveSubjectInsightPort;

    @Mock
    private ApproveAttributeInsightPort approveAttributeInsightPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testApproveAllAssessmentInsights_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveAllAssessmentInsights(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(approveAssessmentInsightPort, approveSubjectInsightPort, approveAttributeInsightPort);
    }

    @Test
    void testApproveAllAssessmentInsights_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.approveAllAssessmentInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(approveAssessmentInsightPort, approveSubjectInsightPort, approveAttributeInsightPort);
    }

    @Test
    void testApproveAllAssessmentInsights_whenAssessmentInsightIsNotApproved_thenApproveAllInsights() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(createDefaultInsightWithAssessmentResultId(assessmentResult.getId())));
        doNothing().when(approveAssessmentInsightPort).approve(eq(param.getAssessmentId()), any(LocalDateTime.class));
        doNothing().when(approveSubjectInsightPort).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));
        doNothing().when(approveAttributeInsightPort).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));

        service.approveAllAssessmentInsights(param);

        verify(approveAssessmentInsightPort, times(1)).approve(eq(param.getAssessmentId()), any(LocalDateTime.class));
        verify(approveSubjectInsightPort, times(1)).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));
        verify(approveAttributeInsightPort, times(1)).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));
    }

    @Test
    void testApproveAllAssessmentInsights_whenAssessmentInsightIsApproved_thenApproveAttributesAndSubjectsInsights() {
        var param = createParam(Param.ParamBuilder::build);
        var insightTime = LocalDateTime.now();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(createDefaultInsightWithTimesAndApprove(insightTime, insightTime, true)));
        doNothing().when(approveSubjectInsightPort).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));
        doNothing().when(approveAttributeInsightPort).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));

        service.approveAllAssessmentInsights(param);

        verify(approveAssessmentInsightPort, never()).approve(any(), any());
        verify(approveSubjectInsightPort, times(1)).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));
        verify(approveAttributeInsightPort, times(1)).approveAll(eq(param.getAssessmentId()), any(LocalDateTime.class));
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}