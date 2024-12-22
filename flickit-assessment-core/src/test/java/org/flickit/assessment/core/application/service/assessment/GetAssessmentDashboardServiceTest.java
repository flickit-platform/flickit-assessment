package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.assessmentdashboard.Insights;
import org.flickit.assessment.core.application.domain.assessmentdashboard.Questions;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerDashboardPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadInsightsDashboardPort;
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


    private final int totalQuestions = 20;
    private final int totalEvidences = 10;
    private final Questions.Answer questionAnswers1 = new Questions.Answer(1L, 1);
    private final Questions.Answer questionAnswers2 = new Questions.Answer(2L, 2);
    private final Questions.Answer questionAnswers3 = new Questions.Answer(3L, 3);
    private final Questions.Evidence evidence1 = new Questions.Evidence(UUID.randomUUID(), 0, null, 124L);
    private final Questions.Evidence evidence2 = new Questions.Evidence(UUID.randomUUID(), 1, null, 125L);
    private final Questions.Evidence evidence3 = new Questions.Evidence(UUID.randomUUID(), 0, null, 125L);
    private final Questions.Evidence evidence4 = new Questions.Evidence(UUID.randomUUID(), null, null,125L);
    private final Questions.Evidence evidence5 = new Questions.Evidence(UUID.randomUUID(), null, null, 126);
    private final Insights.Insight insight1 = new Insights.Insight(UUID.randomUUID(), LocalDateTime.MAX);
    private final Insights.Insight insight2 = new Insights.Insight(UUID.randomUUID(), LocalDateTime.MIN);

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
        long attributeCount = 7;
        long subjectsCount = 2;

        var questionsPortResult = new Questions(questionAnswers,questionsEvidences, totalQuestions, totalEvidences);
        var insightsPortResul = new Insights(insights, attributeCount, subjectsCount);


        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadQuestionsAnswerDashboardPort.loadQuestionsDashboard(assessmentResult.getKitVersionId())).thenReturn(questionsPortResult);
        when(loadInsightsDashboardPort.loadInsights(assessmentResult.getKitVersionId())).thenReturn(insightsPortResul);

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(totalQuestions, result.questions().total());
        assertEquals(questionAnswers.size(), result.questions().answered());
        assertEquals(17, result.questions().unanswered());
        assertEquals(2, result.questions().hasLowConfidence());
        assertEquals(18, result.questions().hasNoEvidence());
        assertEquals(5, result.questions().hasUnresolvedComments());
        //insights
        assertEquals(10, result.insights().total());
        assertEquals(8, result.insights().notGenerated());
        assertEquals(1, result.insights().expired());
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
