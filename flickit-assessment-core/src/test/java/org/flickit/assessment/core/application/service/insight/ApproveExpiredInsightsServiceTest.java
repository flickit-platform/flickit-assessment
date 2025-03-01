package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.insight.ApproveExpiredInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.ApproveAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.ApproveAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.ApproveSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithTimesAndApprove;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.defaultSubjectInsight;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveExpiredInsightsServiceTest {

    @InjectMocks
    private ApproveExpiredInsightsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    @Mock
    private ApproveAttributeInsightPort approveAttributeInsightPort;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Mock
    private ApproveSubjectInsightPort approveSubjectInsightPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private ApproveAssessmentInsightPort approveAssessmentInsightPort;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testApproveExpiredInsights_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveExpiredInsights(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadAttributeInsightsPort,
            approveAttributeInsightPort,
            loadSubjectInsightsPort,
            approveSubjectInsightPort,
            loadAssessmentInsightPort,
            approveAssessmentInsightPort);
    }

    @Test
    void testApproveExpiredInsights_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.approveExpiredInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAttributeInsightsPort,
            approveAttributeInsightPort,
            loadSubjectInsightsPort,
            approveSubjectInsightPort,
            loadAssessmentInsightPort,
            approveAssessmentInsightPort);
    }

    @Test
    void testApproveExpiredInsights_whenCalculatedResultIsNotValid_thenThrowCalculateNotValidException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.approveExpiredInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightsPort,
            approveAttributeInsightPort,
            loadSubjectInsightsPort,
            approveSubjectInsightPort,
            loadAssessmentInsightPort,
            approveAssessmentInsightPort);
    }

    @Test
    void testApproveExpiredInsights_whenOneAttributeInsightIsExpired_thenApproveAttributeInsights() {
        var oldInsightTime = LocalDateTime.now().minusDays(1);
        var expiredAttributeInsight = aiInsightWithTime(oldInsightTime);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(expiredAttributeInsight));

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createWithAssessmentResultId(assessmentResult.getId())));

        service.approveExpiredInsights(param);

        verify(approveAttributeInsightPort, times(1))
            .approveAll(eq(assessmentResult.getAssessment().getId()),
                eq(List.of(expiredAttributeInsight.getAttributeId())),
                notNull(LocalDateTime.class));
        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(approveSubjectInsightPort, approveAssessmentInsightPort);
    }

    @Test
    void testApproveExpiredInsights_whenOneSubjectInsightIsExpired_thenApproveSubjectInsights() {
        var oldInsightTime = LocalDateTime.now().minusDays(1);
        var expiredSubjectInsight = defaultSubjectInsight(oldInsightTime, oldInsightTime, false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(1569L)));

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of(expiredSubjectInsight));

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(AssessmentInsightMother.createSimpleAssessmentInsight()));

        service.approveExpiredInsights(param);

        verify(approveSubjectInsightPort, times(1))
            .approveAll(eq(assessmentResult.getAssessment().getId()),
                eq(List.of(expiredSubjectInsight.getSubjectId())),
                notNull(LocalDateTime.class));
        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(approveAttributeInsightPort, approveAssessmentInsightPort);
    }

    @Test
    void testApproveExpiredInsights_whenAssessmentInsightIsExpired_thenApproveAssessmentInsight() {
        var oldInsightTime = LocalDateTime.now().minusDays(1);
        var expiredAssessmentInsight = createDefaultInsightWithTimesAndApprove(oldInsightTime, oldInsightTime, false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId()))
            .thenReturn(List.of(AttributeInsightMother.aiInsightWithAttributeId(1569L)));

        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId()))
            .thenReturn(List.of());

        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()))
            .thenReturn(Optional.of(expiredAssessmentInsight));

        service.approveExpiredInsights(param);

        verify(approveAssessmentInsightPort, times(1))
            .approve(eq(assessmentResult.getAssessment().getId()), notNull(LocalDateTime.class));
        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(approveAttributeInsightPort, approveSubjectInsightPort);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var newParam = paramBuilder();
        changer.accept(newParam);
        return newParam.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
