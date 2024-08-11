package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.port.in.attribute.UpdateAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAttributeInsightService implements UpdateAttributeInsightUseCase {

    private final GetAssessmentPort getAssessmentPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final UpdateAttributeAssessorInsightPort updateAttributeAssessorInsightPort;

    @Override
    public void updateAttributeInsight(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        if (!assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(assessment.getId());

        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())
            .orElseThrow(()-> new ResourceNotFoundException(UPDATE_ASSESSMENT_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND));

        updateAttributeAssessorInsightPort.updateAssessorInsight(toAttributeInsight(
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
