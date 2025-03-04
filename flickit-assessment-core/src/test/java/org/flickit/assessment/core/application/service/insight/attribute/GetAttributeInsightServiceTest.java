package org.flickit.assessment.core.application.service.insight.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.attribute.GetAttributeInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeInsightServiceTest {

    @InjectMocks
    private GetAttributeInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort assessmentResultPort;

    @Mock
    private GetAttributeInsightHelper getAttributeInsightHelper;

    private final AssessmentResult assessmentResult = validResult();

    @Test
    void testGetAttributeInsight_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(getAttributeInsightHelper, assessmentResultPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getInsight(param));
        assertEquals(GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(getAttributeInsightHelper);
    }

    @Test
    void testGetAttributeInsight_whenHelperReturnsInsight_thenReturnsSameInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var insight = new Insight(null,
            new Insight.InsightDetail("insight", LocalDateTime.now(), true),
            true,
            true,
            LocalDateTime.now());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(getAttributeInsightHelper.getAttributeInsight(assessmentResult, param.getAttributeId(), param.getCurrentUserId()))
            .thenReturn(insight);

        var result = service.getInsight(param);

        assertEquals(insight.defaultInsight(), result.aiInsight());
        assertEquals(insight.assessorInsight(), result.assessorInsight());
        assertEquals(insight.editable(), result.editable());
        assertEquals(insight.approved(), result.approved());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(assessmentResult.getAssessment().getId())
            .attributeId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
