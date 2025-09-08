package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.ClassUtils;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.ClassUtils.countProvidedFields;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentDashboardService implements GetAssessmentDashboardUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CountAdviceItemsPort countAdviceItemsPort;
    private final CountEvidencesPort countEvidencesPort;
    private final CountAttributesPort countAttributesPort;
    private final CountSubjectsPort countSubjectsPort;
    private final LoadAssessmentPort loadAssessmentPort;
    private final CountLowConfidenceAnswersPort countLowConfidenceAnswersPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final LoadAssessmentReportPort loadAssessmentReportPort;
    private final CountAnswersPort countAnswersPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Override
    public Result getAssessmentDashboard(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()).
            orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND));

        var assessmentReport = loadAssessmentReportPort.load(param.getAssessmentId()).orElse(null);

        return new Result(
            buildQuestionsResult(param.getAssessmentId(), assessmentResult.getId()),
            buildInsightsResult(assessmentResult),
            buildAdvices(assessmentResult.getId(), assessmentResult.getLastCalculationTime()),
            buildReport(assessmentReport)
        );
    }

    private Result.Questions buildQuestionsResult(UUID assessmentId, UUID assessmentResultId) {
        var progress = loadAssessmentPort.progress(assessmentId);
        var questionsCount = progress.questionsCount();
        var answersCount = progress.answersCount();
        var lowConfidenceAnswersCount = countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResultId, ConfidenceLevel.SOMEWHAT_UNSURE);
        var answeredQuestionsWithEvidenceCount = countEvidencesPort.countAnsweredQuestionsHavingEvidence(assessmentId);
        var unresolvedCommentsCount = countEvidencesPort.countUnresolvedComments(assessmentId);
        var unapprovedAnswers = countAnswersPort.countUnapprovedAnswers(assessmentResultId);

        return new Result.Questions(
            questionsCount,
            answersCount,
            questionsCount - answersCount,
            lowConfidenceAnswersCount,
            answersCount - answeredQuestionsWithEvidenceCount,
            unresolvedCommentsCount,
            unapprovedAnswers);
    }

    private Result.Insights buildInsightsResult(AssessmentResult assessmentResult) {
        var attributesCount = countAttributesPort.countAttributes(assessmentResult.getKitVersionId());
        var subjectsCount = countSubjectsPort.countSubjects(assessmentResult.getKitVersionId());
        var expectedInsightsCount = attributesCount + subjectsCount + 1;

        var attributesInsights = loadAttributeInsightsPort.loadInsights(assessmentResult.getId());
        var subjectsInsights = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId());
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()).orElse(null);
        var totalGeneratedInsights = attributesInsights.size() + subjectsInsights.size() + (assessmentInsight == null ? 0 : 1);

        var lastCalculationTime = assessmentResult.getLastCalculationTime();

        int unapprovedInsightsCount = countUnapprovedInsights(attributesInsights, subjectsInsights, assessmentInsight);
        int expiredInsightsCount = countExpiredInsights(attributesInsights, subjectsInsights, assessmentInsight, lastCalculationTime);

        int notGenerated = Math.max(expectedInsightsCount - totalGeneratedInsights, 0);
        if (totalGeneratedInsights > expectedInsightsCount) {
            log.error("TotalGeneratedInsights exceeds the expected count: totalExpected:[{}], totalGenerated:[{}]",
                expectedInsightsCount, totalGeneratedInsights);
        }

        return new Result.Insights(
            expectedInsightsCount,
            notGenerated,
            unapprovedInsightsCount,
            expiredInsightsCount);
    }

    private int countUnapprovedInsights(List<AttributeInsight> attributesInsights,
                                        List<SubjectInsight> subjectsInsights,
                                        AssessmentInsight assessmentInsight) {
        var unapprovedAttributeInsightsCount = (int) attributesInsights.stream()
            .filter(e -> !e.isApproved())
            .count();

        var unapprovedSubjectsInsightsCount = (int) subjectsInsights.stream()
            .filter(e -> !e.isApproved())
            .count();

        int assessmentInsightUnapproved = assessmentInsight != null && !assessmentInsight.isApproved() ? 1 : 0;

        return unapprovedAttributeInsightsCount + unapprovedSubjectsInsightsCount + assessmentInsightUnapproved;
    }

    private int countExpiredInsights(List<AttributeInsight> attributesInsights,
                                     List<SubjectInsight> subjectsInsights,
                                     AssessmentInsight assessmentInsight,
                                     LocalDateTime lastCalculationTime) {
        var expiredAttributeInsightsCount = (int) attributesInsights.stream()
            .filter(e -> e.getLastModificationTime().isBefore(lastCalculationTime))
            .count();

        var expiredSubjectsInsightsCount = (int) subjectsInsights.stream()
            .filter(e -> e.getLastModificationTime().isBefore(lastCalculationTime))
            .count();

        int assessmentInsightExpired = assessmentInsight != null && assessmentInsight.getLastModificationTime().isBefore(lastCalculationTime) ? 1 : 0;

        return expiredAttributeInsightsCount + expiredSubjectsInsightsCount + assessmentInsightExpired;
    }

    private Result.Advices buildAdvices(UUID assessmentResultId, LocalDateTime lastCalculationTime) {
        int adviceItemsCount = countAdviceItemsPort.countByAssessmentResultId(assessmentResultId);

        return loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResultId)
            .map(narration -> {
                int expired = narration.getLastModificationTime().isBefore(lastCalculationTime) ? 1 : 0;
                int unapproved = narration.isApproved() ? 0 : 1;
                return new Result.Advices(adviceItemsCount, unapproved, expired);
            })
            .orElseGet(() -> new Result.Advices(adviceItemsCount, 0, 0));
    }

    private Result.Report buildReport(AssessmentReport assessmentReport) {
        int allFieldsCount = ClassUtils.countAllFields(AssessmentReportMetadata.class);

        if (assessmentReport == null)
            return new Result.Report(true, allFieldsCount, 0, allFieldsCount);
        if (assessmentReport.getMetadata() == null)
            return new Result.Report(!assessmentReport.isPublished(), allFieldsCount, 0, allFieldsCount);

        int providedFieldsCount = countProvidedFields(assessmentReport.getMetadata());

        return new Result.Report(!assessmentReport.isPublished(),
            Math.max(allFieldsCount - providedFieldsCount, 0),
            providedFieldsCount,
            allFieldsCount);
    }
}
