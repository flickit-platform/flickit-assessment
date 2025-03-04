package org.flickit.assessment.core.application.service.insight.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.domain.insight.Insight.InsightDetail;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeInsightHelper {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final LoadAttributeInsightsPort loadAttributeInsightsPort;

    public Insight getAttributeInsight(AssessmentResult assessmentResult, Long attributeId, UUID currentUserId) {
        var editable = assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT);
        var attributeInsight = loadAttributeInsightPort.load(assessmentResult.getId(), attributeId);

        return attributeInsight
            .map(insight -> isAiInsightValid(insight)
                ? getAiInsight(assessmentResult, insight, editable)
                : getDefaultInsight(assessmentResult, insight, editable))
            .orElse(new Insight(null, null, editable, null, null));
    }

    public Map<Long, Insight> getAttributeInsights(AssessmentResult assessmentResult, UUID currentUserId) {
        var editable = assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT);
        var attributeInsights = loadAttributeInsightsPort.loadInsights(assessmentResult.getId());

        return attributeInsights.stream()
            .collect(toMap(AttributeInsight::getAttributeId, insight -> isAiInsightValid(insight)
                ? getAiInsight(assessmentResult, insight, editable)
                : getDefaultInsight(assessmentResult, insight, editable)));
    }

    private boolean isAiInsightValid(AttributeInsight insight) {
        return insight.getAssessorInsight() == null ||
            (insight.getAiInsightTime() != null && insight.getAiInsightTime().isAfter(insight.getAssessorInsightTime()));
    }

    private Insight getAiInsight(AssessmentResult assessmentResult, AttributeInsight insight, boolean editable) {
        var aiInsight = new InsightDetail(insight.getAiInsight(),
            insight.getAiInsightTime(),
            isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime()));
        return new Insight(aiInsight, null, editable, insight.isApproved(), insight.getLastModificationTime());
    }

    private Insight getDefaultInsight(AssessmentResult assessmentResult, AttributeInsight insight, boolean editable) {
        var assessorInsight = new InsightDetail(insight.getAssessorInsight(),
            insight.getAssessorInsightTime(),
            isValid(assessmentResult.getLastCalculationTime(), insight.getLastModificationTime()));
        return new Insight(null, assessorInsight, editable, insight.isApproved(), insight.getLastModificationTime());
    }

    private boolean isValid(LocalDateTime lastCalculationTime, LocalDateTime insightLastModificationTime) {
        return lastCalculationTime.isBefore(insightLastModificationTime);
    }
}
