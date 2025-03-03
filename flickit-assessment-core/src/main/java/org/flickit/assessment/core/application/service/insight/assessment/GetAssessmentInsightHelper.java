package org.flickit.assessment.core.application.service.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInsightHelper {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentInsightPort loadAssessmentInsightPort;

    public Insight getAssessmentInsight(AssessmentResult assessmentResult, UUID currentUserId) {
        var editable = assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ASSESSMENT_INSIGHT);
        var assessmentInsight = loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId());

        return assessmentInsight
            .map(insight -> (insight.getInsightBy() == null)
                ? getDefaultInsight(assessmentResult.getLastCalculationTime(), insight, editable)
                : getAssessorInsight(assessmentResult.getLastCalculationTime(), insight, editable))
            .orElseGet(() -> new Insight(null, null, editable, false));
    }

    private Insight getDefaultInsight(LocalDateTime lastCalculationTime, AssessmentInsight insight, boolean editable) {
        return new Insight(new Insight.InsightDetail(insight.getInsight(),
            insight.getInsightTime(),
            isValid(lastCalculationTime, insight.getLastModificationTime())),
            null,
            editable,
            insight.isApproved());
    }

    private Insight getAssessorInsight(LocalDateTime lastCalculationTime, AssessmentInsight insight, boolean editable) {
        return new Insight(null,
            new Insight.InsightDetail(insight.getInsight(),
                insight.getInsightTime(),
                isValid(lastCalculationTime, insight.getLastModificationTime())),
            editable,
            insight.isApproved());
    }

    private boolean isValid(LocalDateTime lastCalculationTime, LocalDateTime insightLastModificationTime) {
        return lastCalculationTime.isBefore(insightLastModificationTime);
    }
}
