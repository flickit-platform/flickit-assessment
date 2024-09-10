package org.flickit.assessment.core.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED;

@Service
@Transactional
@RequiredArgsConstructor
public class GetAttributeInsightService implements GetAttributeInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final LoadAttributePort loadAttributePort;
    private final OpenAiProperties openAiProperties;

    @Override
    public Result getInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        boolean editable = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT);
        var attributeInsight = loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId());
        Attribute attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());

        if (attributeInsight.isEmpty()) {
            if (!openAiProperties.isEnabled()) {
                var aiInsight = new Result.Insight(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED,
                    attribute.getTitle()), null, false);
                return new Result(aiInsight, null, false);
            }
            return new Result(null, null, editable);
        }

        var insight = attributeInsight.get();

        Result.Insight aiInsight;
        Result.Insight assessorInsight;
        if (insight.getAssessorInsight() == null) {
            aiInsight = new Result.Insight(insight.getAiInsight(),
                insight.getAiInsightTime(),
                assessmentResult.getLastCalculationTime().isBefore(insight.getAiInsightTime()));
            return new Result(aiInsight, null, editable);
        }
        assessorInsight = new Result.Insight(insight.getAssessorInsight(),
            insight.getAssessorInsightTime(),
            assessmentResult.getLastCalculationTime().isBefore(insight.getAssessorInsightTime()));
        return new Result(null, assessorInsight, editable);
    }
}
