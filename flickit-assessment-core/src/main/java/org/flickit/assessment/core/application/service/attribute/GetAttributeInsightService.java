package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class GetAttributeInsightService implements GetAttributeInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;

    @Override
    public Result getInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND));

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT);

        Result.AiInsight aiInsight;
        Result.AssessorInsight assessorInsight;
        if (attributeInsight.getAssessorInsight() == null) {
            aiInsight = new Result.AiInsight(attributeInsight.getAiInsight(), attributeInsight.getAiInsightTime());
            return new Result(aiInsight, null, editable);
        }
        assessorInsight = new Result.AssessorInsight(attributeInsight.getAssessorInsight(),
            attributeInsight.getAssessorInsightTime(),
            assessmentResult.getLastCalculationTime().isBefore(attributeInsight.getAiInsightTime()));
        return new Result(null, assessorInsight, editable);
    }
}
