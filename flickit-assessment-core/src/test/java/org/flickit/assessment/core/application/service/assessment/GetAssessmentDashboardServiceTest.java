package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentDashboardUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.CountAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightsPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.subject.CountSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightsPort;
import org.flickit.assessment.core.test.fixture.application.*;
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
    private CountSubjectsPort countSubjectsPort;

    @Mock
    private CountAttributesPort countAttributesPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private LoadSubjectInsightsPort loadSubjectInsightsPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CountEvidencesPort countEvidencesPort;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    private final int attributeCount = 7;
    private final int subjectsCount = 2;
    private final int questionCount = 15;
    private final int answerCount = 10;
    private final int unResolveCommentsCount = 1;
    private final int questionsWithEvidenceCount = 3;

    private final AttributeInsight attributeInsight1 = AttributeInsightMother.simpleAttributeInsight();
    private final AttributeInsight attributeInsight2 = AttributeInsightMother.simpleAttributeInsightMinInsightTime();
    private final AttributeInsight attributeInsight3 = AttributeInsightMother.simpleAttributeInsightMinInsightsTime();

    private final SubjectInsight subjectInsight1 = SubjectInsightMother.subjectInsight();
    private final SubjectInsight subjectInsight2 = SubjectInsightMother.subjectInsight();
    private final SubjectInsight subjectInsight3 = SubjectInsightMother.subjectInsightMinInsightTime();

    @Test
    void testGetAssessmentDashboard_WhenUserDoesNotHaveRequiredPermission_ThenThrowsAccessDeniedException() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentDashboard(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAssessmentDashboard_WhenAssessmentInsightExistsAndAssessmentReportHasFullProvidedMetadata_ThenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentInsight = AssessmentInsightMother.createSimpleAssessmentInsight();
        var metadata = AssessmentReportMetadataMother.createWithFullMetadata();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(new GetAssessmentProgressPort.Result(param.getAssessmentId(), answerCount, questionCount));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unResolveCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(AssessmentReportMother.publishedReportWithMetadata(metadata)));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(3, result.insights().notGenerated());
        assertEquals(2, result.insights().expired());
        //advices
        assertEquals(2, result.advices().total());
        //report
        assertFalse(result.report().unpublished());
        assertEquals(0, result.report().unprovidedMetadata());
    }

    @Test
    void testGetAssessmentDashboard_WhenAssessmentInsightNotExistsAndAssessmentReportHasPartialMetadata_ThenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var metadata = AssessmentReportMetadataMother.createWithPartialMetadata();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(new GetAssessmentProgressPort.Result(param.getAssessmentId(), answerCount, questionCount));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unResolveCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(AssessmentReportMother.publishedReportWithMetadata(metadata)));

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(4, result.insights().notGenerated());
        assertEquals(2, result.insights().expired());
        //advices
        assertEquals(2, result.advices().total());
        //report
        assertFalse(result.report().unpublished());
        assertEquals(3, result.report().unprovidedMetadata());
    }

    @Test
    void testGetAssessmentDashboard_WhenAssessmentInsightExpiredAndNullAssessmentReport_ThenProduceResult() {
        var param = createParam(GetAssessmentDashboardUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentInsight = AssessmentInsightMother.createWithMinInsightTime();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_DASHBOARD)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResult.getId(), ConfidenceLevel.SOMEWHAT_UNSURE)).thenReturn(2);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight1, attributeInsight2, attributeInsight3));
        when(loadAdvicesDashboardPort.countAdviceItems(assessmentResult.getId())).thenReturn(2);
        when(countSubjectsPort.countSubjects(assessmentResult.getKitVersionId())).thenReturn(subjectsCount);
        when(countAttributesPort.countAttributes(assessmentResult.getKitVersionId())).thenReturn(attributeCount);
        when(getAssessmentProgressPort.getProgress(param.getAssessmentId())).thenReturn(new GetAssessmentProgressPort.Result(param.getAssessmentId(), answerCount, questionCount));
        when(loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId())).thenReturn(List.of(subjectInsight1, subjectInsight2, subjectInsight3));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(countEvidencesPort.countUnresolvedComments(param.getAssessmentId())).thenReturn(unResolveCommentsCount);
        when(countEvidencesPort.countAnsweredQuestionsHavingEvidence(param.getAssessmentId())).thenReturn(questionsWithEvidenceCount);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        var result = service.getAssessmentDashboard(param);
        //questions
        assertEquals(questionCount, result.questions().total());
        assertEquals(answerCount, result.questions().answered());
        assertEquals(5, result.questions().unanswered());
        assertEquals(2, result.questions().answeredWithLowConfidence());
        assertEquals(7, result.questions().withoutEvidence());
        assertEquals(1, result.questions().unresolvedComments());
        //insights
        assertEquals(10, result.insights().expected());
        assertEquals(3, result.insights().notGenerated());
        assertEquals(3, result.insights().expired());
        //advices
        assertEquals(2, result.advices().total());
        //report
        assertTrue(result.report().unpublished());
        assertEquals(4, result.report().unprovidedMetadata());
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
