package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.port.in.attribute.UpdateAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAttributeInsightService implements UpdateAttributeInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;

    @Override
    public void updateAttributeInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())
            .orElseThrow(()-> new ResourceNotFoundException(UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND));

        updateAttributeInsightPort.updateAssessorInsight(toAttributeInsight(
            assessmentResult.getId(),
            attributeInsight.getAttributeId(),
            param.getAssessorInsight()));
    }

    private static AttributeInsight toAttributeInsight(UUID assessmentResultId, long attributeId, String assessorInsight) {
        return new AttributeInsight(assessmentResultId,
            attributeId,
            null,
            assessorInsight,
            null,
            LocalDateTime.now(),
            null);
    }
}
