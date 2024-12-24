package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesDashboardPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentDashboardServiceTest {

    @InjectMocks
    private GetAssessmentDashboardService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CountLowConfidenceAnswersPort countLowConfidenceAnswersPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    @Mock
    private CountAdviceItemsPort loadAdvicesDashboardPort;

    @Mock
    private LoadEvidencesDashboardPort loadEvidencesDashboardPort;

    @Mock
    private CountSubjectsPort countSubjectsPort;

    @Mock
    private CountAttributesPort countAttributesPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    private final LoadEvidencesDashboardPort.Result.Evidence evidence1 = new LoadEvidencesDashboardPort.Result.Evidence(UUID.randomUUID(), 0, null, 124L);
    private final LoadEvidencesDashboardPort.Result.Evidence evidence2 = new LoadEvidencesDashboardPort.Result.Evidence(UUID.randomUUID(), 1, null, 125L);
    private final LoadEvidencesDashboardPort.Result.Evidence evidence3 = new LoadEvidencesDashboardPort.Result.Evidence(UUID.randomUUID(), 0, null, 125L);
    private final LoadEvidencesDashboardPort.Result.Evidence evidence4 = new LoadEvidencesDashboardPort.Result.Evidence(UUID.randomUUID(), null, null, 125L);
    private final LoadEvidencesDashboardPort.Result.Evidence evidence5 = new LoadEvidencesDashboardPort.Result.Evidence(UUID.randomUUID(), null, true, 126);


    @Test
    void testGetAssessmentDashboard_userDoesNotHaveAccess_throwsAccessDeniedException() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentDashboard(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAssessmentDashboard_validParams_produceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);

        var assessmentResult = AssessmentResultMother.validResult();
        var insight1 = AttributeInsightMother.simpleAttributeAiInsight();
        var insight2 = AttributeInsightMother.simpleAttributeAiInsightMinInsightTime();
        var insight3 = AttributeInsightMother.simpleAttributeAiInsightMinInsightsTime();

        var questionsEvidences = List.of(evidence1, evidence2, evidence3, evidence4, evidence5);
        int attributeCount = 7;
        int subjectsCount = 2;
        int questionCount = 15;
        int answerCount = 10;
        var evidencesPortResult = new LoadEvidencesDashboardPort.Result(questionsEvidences);

        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadEvidencesDashboardPort.loadEvidencesDashboard(param.getId())).thenReturn(evidencesPortResult);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(insight1, insight2, insight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(new CountAdviceItemsPort.Result(2));
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(getAssessmentProgressPort.getProgress(param.getId())).thenReturn(new GetAssessmentProgressPort.Result(param.getId(), answerCount, questionCount));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().hasLowConfidence());
        assertEquals(13, result.questions().hasNoEvidence());
        assertEquals(1, result.questions().hasUnresolvedComments());
        //insights
        assertEquals(10, result.insights().total());
        assertEquals(7, result.insights().notGenerated());
        assertEquals(1, result.insights().expired());
        //advices
        assertEquals(2, result.advices().total());
    }

    private GetAssessmentDashboardUseCase.Param createParam(Consumer<GetAssessmentDashboardUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentDashboardUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentDashboardUseCase.Param.builder()
            .id(UUID.randomUUID())
            .currentUserId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
