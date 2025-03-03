package org.flickit.assessment.core.application.service.insight.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.domain.insight.Insight.InsightDetail;
import org.flickit.assessment.core.application.domain.insight.SubjectInsight;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSubjectInsightHelper {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadSubjectInsightPort loadSubjectInsightPort;
    private final LoadSubjectInsightsPort loadSubjectInsightsPort;

    public Insight getSubjectInsight(AssessmentResult assessmentResult, Long subjectId, UUID currentUserId) {
        var editable = assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT);
        var subjectInsight = loadSubjectInsightPort.load(assessmentResult.getId(), subjectId);

        return subjectInsight
            .map(insight ->
                insight.getInsightBy() == null
                    ? getDefaultInsight(assessmentResult, insight, editable)
                    : getAssessorInsight(assessmentResult, insight, editable))
            .orElse(new Insight(null, null, editable, false));
    }

    public Map<Long, Insight> getSubjectsInsight(AssessmentResult assessmentResult, UUID currentUserId) {
        var editable = assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_SUBJECT_INSIGHT);
        var subjectInsights = loadSubjectInsightsPort.loadSubjectInsights(assessmentResult.getId());

        return subjectInsights.stream()
            .collect(toMap(SubjectInsight::getSubjectId,
                insight ->
                    insight.getInsightBy() == null
                        ? getDefaultInsight(assessmentResult, insight, editable)
                        : getAssessorInsight(assessmentResult, insight, editable)
            ));
    }

    private Insight getDefaultInsight(AssessmentResult assessmentResult, SubjectInsight insight, boolean editable) {
        return new Insight(new InsightDetail(insight.getInsight(),
            insight.getInsightTime(),
            isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime())),
            null,
            editable,
            insight.isApproved());
    }

    private Insight getAssessorInsight(AssessmentResult assessmentResult, SubjectInsight insight, boolean editable) {
        return new Insight(null,
            new InsightDetail(insight.getInsight(),
                insight.getInsightTime(),
                isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime())),
            editable,
            insight.isApproved());
    }

    private boolean isValid(LocalDateTime lastCalculationTime, LocalDateTime insightLastModificationTime) {
        return lastCalculationTime.isBefore(insightLastModificationTime);
    }
}
