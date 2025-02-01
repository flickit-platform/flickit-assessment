package org.flickit.assessment.core.application.service.assessment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_DASHBOARD_ASSESSMENT_RESULT_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentDashboardService implements GetAssessmentDashboardUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;
    private final CountAdviceItemsPort loadAdvicesDashboardPort;
    private final CountEvidencesPort countEvidencesPort;
    private final CountAttributesPort countAttributesPort;
    private final CountSubjectsPort countSubjectsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final CountLowConfidenceAnswersPort countLowConfidenceAnswersPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;
    private final LoadAssessmentReportPort loadAssessmentReportPort;

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
            buildAdvices(assessmentResult.getId()),
            buildReport(assessmentReport)
        );
    }

    private Result.Questions buildQuestionsResult(UUID assessmentId, UUID assessmentResultId) {
        var progress = getAssessmentProgressPort.getProgress(assessmentId);
        var questionsCount = progress.questionsCount();
        var answersCount = progress.answersCount();
        var lowConfidenceAnswersCount = countLowConfidenceAnswersPort.countWithConfidenceLessThan(assessmentResultId, ConfidenceLevel.SOMEWHAT_UNSURE);
        var answeredQuestionsWithEvidenceCount = countEvidencesPort.countAnsweredQuestionsHavingEvidence(assessmentId);
        var unresolvedCommentsCount = countEvidencesPort.countUnresolvedComments(assessmentId);

        return new Result.Questions(
            questionsCount,
            answersCount,
            questionsCount - answersCount,
            lowConfidenceAnswersCount,
            answersCount - answeredQuestionsWithEvidenceCount,
            unresolvedCommentsCount);
    }

    private Result.Insights buildInsightsResult(AssessmentResult assessmentResult) {
        var attributesCount = countAttributesPort.countAttributes(assessmentResult.getKitVersionId());
        var subjectsCount = countSubjectsPort.countSubjects(assessmentResult.getKitVersionId());
        var expectedInsightsCount = attributesCount + subjectsCount + 1;

        var attributeInsights = loadAttributeInsightsPort.loadInsights(assessmentResult.getId());
        var subjectsInsights = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId());
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId()).orElse(null);
        var totalGeneratedInsights = attributeInsights.size() + subjectsInsights.size() + (assessmentInsight == null ? 0 : 1);

        var lastCalculationTime = assessmentResult.getLastCalculationTime();
        var expiredAttributeInsightsCount = Math.toIntExact(attributeInsights.stream()
            .map(e -> {
                if (e.getAiInsightTime() == null) {
                    return e.getAssessorInsightTime();
                } else if (e.getAssessorInsightTime() == null) {
                    return e.getAiInsightTime();
                } else {
                    return e.getAiInsightTime().isBefore(e.getAssessorInsightTime())
                        ? e.getAssessorInsightTime()
                        : e.getAiInsightTime();
                }
            })
            .filter(latestInsightTime -> latestInsightTime != null && latestInsightTime.isBefore(lastCalculationTime))
            .count());

        var expiredSubjectsInsightsCount = Math.toIntExact(subjectsInsights.stream()
            .filter(e -> e.getInsightTime().isBefore(lastCalculationTime))
            .count());

        int assessmentInsightExpired = assessmentInsight != null && assessmentInsight.getInsightTime().isBefore(lastCalculationTime) ? 1 : 0;

        int notGenerated = Math.max(expectedInsightsCount - totalGeneratedInsights, 0);
        if (totalGeneratedInsights > expectedInsightsCount) {
            log.error("TotalGeneratedInsights exceeds the expected count: totalExpected:[{}], totalGenerated:[{}]",
                expectedInsightsCount, totalGeneratedInsights);
        }

        return new Result.Insights(
            expectedInsightsCount,
            notGenerated,
            0,
            expiredAttributeInsightsCount + expiredSubjectsInsightsCount + assessmentInsightExpired
        );
    }

    private Result.Advices buildAdvices(UUID assessmentResultId) {
        var adviceItemsCount = loadAdvicesDashboardPort.countAdviceItems(assessmentResultId);
        return new Result.Advices(adviceItemsCount);
    }

    private Result.Report buildReport(AssessmentReport assessmentReport) {
        int allFieldsCount = AssessmentReportMetadata.class.getDeclaredFields().length;
        if (assessmentReport == null || assessmentReport.getMetadata() == null)
            return new Result.Report(true, allFieldsCount);

        Map<String, Object> notNullFields = new ObjectMapper().convertValue(assessmentReport.getMetadata(), new TypeReference<>() {
        });
        int notEmptyFieldsCount = Math.toIntExact(notNullFields
            .entrySet()
            .stream()
            .filter(entry -> {
                String str = (String) entry.getValue();
                return !str.isBlank();
            })
            .count());

        return new Result.Report(!assessmentReport.isPublished(), Math.max(allFieldsCount - notEmptyFieldsCount, 0));
    }
}
