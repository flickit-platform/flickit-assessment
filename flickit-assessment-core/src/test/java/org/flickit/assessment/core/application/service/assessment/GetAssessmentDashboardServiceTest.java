package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardEvidences;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardInsights;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAnswersQuestions;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;
import org.flickit.assessment.core.application.port.out.advice.CountAdvicesDashboardPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerDashboardPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadInsightsDashboardPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesDashboardPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
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
    private LoadQuestionsAnswerDashboardPort loadQuestionsAnswerDashboardPort;

    @Mock
    private LoadInsightsDashboardPort loadInsightsDashboardPort;

    @Mock
    private CountAdvicesDashboardPort loadAdvicesDashboardPort;

    @Mock
    private LoadEvidencesDashboardPort loadEvidencesDashboardPort;

    @Mock
    private CountSubjectsPort countSubjectsPort;

    @Mock
    private CountAttributesPort countAttributesPort;


    private final DashboardAnswersQuestions.Answer questionAnswers1 = new DashboardAnswersQuestions.Answer(UUID.randomUUID(), 1);
    private final DashboardAnswersQuestions.Answer questionAnswers2 = new DashboardAnswersQuestions.Answer(UUID.randomUUID(), 2);
    private final DashboardAnswersQuestions.Answer questionAnswers3 = new DashboardAnswersQuestions.Answer(UUID.randomUUID(), 3);
    private final DashboardEvidences.Evidence evidence1 = new DashboardEvidences.Evidence(UUID.randomUUID(), 0, null, 124L);
    private final DashboardEvidences.Evidence evidence2 = new DashboardEvidences.Evidence(UUID.randomUUID(), 1, null, 125L);
    private final DashboardEvidences.Evidence evidence3 = new DashboardEvidences.Evidence(UUID.randomUUID(), 0, null, 125L);
    private final DashboardEvidences.Evidence evidence4 = new DashboardEvidences.Evidence(UUID.randomUUID(), null, null,125L);
    private final DashboardEvidences.Evidence evidence5 = new DashboardEvidences.Evidence(UUID.randomUUID(), null, true, 126);
    private final DashboardInsights.InsightTime insight1 = new DashboardInsights.InsightTime(LocalDateTime.MAX);
    private final DashboardInsights.InsightTime insight2 = new DashboardInsights.InsightTime(LocalDateTime.MIN);

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

        var questionAnswers = List.of(questionAnswers1, questionAnswers2, questionAnswers3);
        var questionsEvidences = List.of(evidence1, evidence2, evidence3, evidence4, evidence5);
        var insights = List.of(insight1, insight2);
        int attributeCount = 7;
        int subjectsCount = 2;
        long totalQuestions = 20;
        var questionAnswerPortResult = new DashboardAnswersQuestions(questionAnswers, totalQuestions);
        var evidencesPortResult = new DashboardEvidences(questionsEvidences);

        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionsAnswerDashboardPort.loadQuestionsDashboard(assessmentResult.getId(), assessmentResult.getKitVersionId())).thenReturn(questionAnswerPortResult);
        when(loadEvidencesDashboardPort.loadEvidencesDashboard(param.getId())).thenReturn(evidencesPortResult);
        when(loadInsightsDashboardPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(insight1, insight2));
        //when(loadAdvicesDashboardPort.loadAdviceDashboard()).thenReturn(new DashboardAdvices(2));
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(totalQuestions, result.questions().total());
        assertEquals(questionAnswers.size(), result.questions().answered());
        assertEquals(17, result.questions().unanswered());
        assertEquals(2, result.questions().hasLowConfidence());
        assertEquals(18, result.questions().hasNoEvidence());
        assertEquals(1, result.questions().hasUnresolvedComments());
        //insights
        assertEquals(10, result.insights().total());
        assertEquals(8, result.insights().notGenerated());
        assertEquals(1, result.insights().expired());
        //advices
        //assertEquals(2, result.advices().total());
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
