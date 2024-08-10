package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.port.in.attribute.UpdateAssessmentAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeAssessorInsightPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.ATTRIBUTE_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UpdateAssessmentAttributeInsightService implements UpdateAssessmentAttributeInsightUseCase {

    private final GetAssessmentPort getAssessmentPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final UpdateAttributeAssessorInsightPort updateAttributeAssessorInsightPort;

    @Override
    public void updateAttributeInsight(Param param) {
        var assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessment.getId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())
            .orElseThrow(()-> new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND));

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
