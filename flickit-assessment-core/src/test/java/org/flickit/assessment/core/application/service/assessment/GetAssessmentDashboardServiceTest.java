package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersPort;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMetadataMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AdviceNarrationMother.aiAdviceNarrationWithTime;
import static org.flickit.assessment.core.test.fixture.application.AdviceNarrationMother.assessorAdviceNarrationWithTime;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.insightWithTimeAndApproved;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.subjectInsight;
import static org.flickit.assessment.core.test.fixture.application.SubjectInsightMother.subjectInsightMinInsightTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
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
    private CountSubjectsPort countSubjectsPort;

    @Mock
    private CountAttributesPort countAttributesPort;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private CountAnswersPort countAnswersPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    private final int attributeCount = 7;
    private final int subjectsCount = 2;
    private final int questionCount = 15;
    private final int answerCount = 10;
    private final int unresolvedCommentsCount = 1;
    private final int questionsWithEvidenceCount = 3;
    private final int unapprovedAnswersCount = 4;

    private final AttributeInsight attributeInsight1 = insightWithTimeAndApproved(LocalDateTime.now().plusSeconds(10), true);
    private final AttributeInsight attributeInsight2 = insightWithTimeAndApproved(LocalDateTime.now().plusSeconds(10), true);
    private final AttributeInsight attributeInsight3 = insightWithTimeAndApproved(LocalDateTime.MIN, false);

    private final SubjectInsight subjectInsight1 = subjectInsight();
    private final SubjectInsight subjectInsight2 = subjectInsight();
    private final SubjectInsight subjectInsight3 = subjectInsightMinInsightTime();

    @Test
    void testGetAssessmentDashboard_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentDashboard(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            countLowConfidenceAnswersPort,
            loadSubjectInsightsPort,
            loadAssessmentInsightPort,
            loadAssessmentReportPort,
            countEvidencesPort,
            loadAssessmentPort,
            countAttributesPort,
            countSubjectsPort,
            countAnswersPort,
            loadAdviceNarrationPort);
    }

    @Test
    void testGetAssessmentDashboard_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentDashboard(param));
        assertEquals(GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(countLowConfidenceAnswersPort,
            loadSubjectInsightsPort,
            loadAssessmentInsightPort,
            loadAssessmentReportPort,
            countEvidencesPort,
            loadAssessmentPort,
            countAttributesPort,
            countSubjectsPort,
            countAnswersPort,
            loadAdviceNarrationPort);
    }

    @Test
    void testGetAssessmentDashboard_whenAssessmentInsightExistsAndAssessmentReportHasFullProvidedMetadata_thenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentInsight = AssessmentInsightMother.createSimpleAssessmentInsight();
        var metadata = AssessmentReportMetadataMother.fullMetadata();
        var aiNarration = aiAdviceNarrationWithTime(LocalDateTime.MIN);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentPort.progress(param.getAssessmentId())).thenReturn(new LoadAssessmentPort.ProgressResult(param.getAssessmentId(), answerCount, questionCount));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(AssessmentReportMother.publishedReportWithMetadata(metadata)));
        when(countAnswersPort.countUnapprovedAnswers(assessmentResult.getId())).thenReturn(unapprovedAnswersCount);
        when(loadAdviceNarrationPort.loadAdviceNarration(assessmentResult.getId())).thenReturn(Optional.of(aiNarration));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        assertEquals(4, result.questions().unapprovedAnswers());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(3, result.insights().notGenerated());
        assertEquals(3, result.insights().unapproved());
        assertEquals(2, result.insights().expired());
        //advices
        assertEquals(2, result.advices().total());
        assertEquals(1, result.advices().unapproved());
        assertEquals(1, result.advices().expired());
        //report
        assertFalse(result.report().unpublished());
        assertEquals(0, result.report().unprovidedMetadata());
        assertEquals(4, result.report().providedMetadata());
        assertEquals(4, result.report().totalMetadata());
    }

    @Test
    void testGetAssessmentDashboard_whenAssessmentInsightNotExistsAndAssessmentReportHasPartialMetadata_thenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var metadata = AssessmentReportMetadataMother.partialMetadata();
        var assessorNarration = assessorAdviceNarrationWithTime(LocalDateTime.MAX);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(loadAssessmentPort.progress(param.getAssessmentId())).thenReturn(new LoadAssessmentPort.ProgressResult(param.getAssessmentId(), answerCount, questionCount));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(AssessmentReportMother.publishedReportWithMetadata(metadata)));
        when(countAnswersPort.countUnapprovedAnswers(assessmentResult.getId())).thenReturn(unapprovedAnswersCount);
        when(loadAdviceNarrationPort.loadAdviceNarration(assessmentResult.getId())).thenReturn(Optional.of(assessorNarration));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        assertEquals(4, result.questions().unapprovedAnswers());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(4, result.insights().notGenerated());
        assertEquals(2, result.insights().unapproved());
        assertEquals(2, result.insights().expired());
        //advices
        assertEquals(2, result.advices().total());
        assertEquals(0, result.advices().unapproved());
        assertEquals(0, result.advices().expired());
        //report
        assertFalse(result.report().unpublished());
        assertEquals(3, result.report().unprovidedMetadata());
        assertEquals(1, result.report().providedMetadata());
        assertEquals(4, result.report().totalMetadata());
    }

    @Test
    void testGetAssessmentDashboard_whenAssessmentInsightExpiredAndNullAssessmentReport_thenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentInsight = AssessmentInsightMother.createWithMinInsightTime();
        var aiNarration = aiAdviceNarrationWithTime(LocalDateTime.MAX);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(loadAssessmentPort.progress(param.getAssessmentId())).thenReturn(new LoadAssessmentPort.ProgressResult(param.getAssessmentId(), answerCount, questionCount));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());
        when(countAnswersPort.countUnapprovedAnswers(assessmentResult.getId())).thenReturn(unapprovedAnswersCount);
        when(loadAdviceNarrationPort.loadAdviceNarration(assessmentResult.getId())).thenReturn(Optional.of(aiNarration));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        assertEquals(4, result.questions().unapprovedAnswers());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(3, result.insights().notGenerated());
        assertEquals(3, result.insights().unapproved());
        assertEquals(3, result.insights().expired());
        assertEquals(4, result.questions().unapprovedAnswers());
        //advices
        assertEquals(2, result.advices().total());
        assertEquals(1, result.advices().unapproved());
        assertEquals(0, result.advices().expired());
        //report
        assertTrue(result.report().unpublished());
        assertEquals(4, result.report().unprovidedMetadata());
        assertEquals(0, result.report().providedMetadata());
        assertEquals(4, result.report().totalMetadata());
    }

    @Test
    void testGetAssessmentDashboard_whenAssessorAdviceNarrationExpired_thenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentInsight = AssessmentInsightMother.createWithMinInsightTime();
        var aiNarration = assessorAdviceNarrationWithTime(LocalDateTime.MIN);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(loadAssessmentPort.progress(param.getAssessmentId())).thenReturn(new LoadAssessmentPort.ProgressResult(param.getAssessmentId(), answerCount, questionCount));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unresolvedCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());
        when(countAnswersPort.countUnapprovedAnswers(assessmentResult.getId())).thenReturn(unapprovedAnswersCount);
        when(loadAdviceNarrationPort.loadAdviceNarration(assessmentResult.getId())).thenReturn(Optional.of(aiNarration));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        assertEquals(4, result.questions().unapprovedAnswers());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(3, result.insights().notGenerated());
        assertEquals(3, result.insights().unapproved());
        assertEquals(3, result.insights().expired());
        assertEquals(4, result.questions().unapprovedAnswers());
        //advices
        assertEquals(2, result.advices().total());
        assertEquals(0, result.advices().unapproved());
        assertEquals(1, result.advices().expired());
        //report
        assertTrue(result.report().unpublished());
        assertEquals(4, result.report().unprovidedMetadata());
        assertEquals(0, result.report().providedMetadata());
        assertEquals(4, result.report().totalMetadata());
    }

    @Test
    void testGetAssessmentDashboard_WhenMetadataFieldsChange_ThenDetectIssue() {
        assertEquals(4, AssessmentReportMetadata.class.getDeclaredFields().length,
            "Developers should be aware that newly added fields may affect how 'report issues' are displayed on the dashboard.");
    }

    private GetAssessmentDashboardUseCase.Param createParam(Consumer<GetAssessmentDashboardUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentDashboardUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentDashboardUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
