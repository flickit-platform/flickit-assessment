package org.flickit.assessment.core.application.service.insight.attribute;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.port.in.insight.attribute.CreateAttributeAiInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.insight.attribute.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAttributeAiInsightService implements CreateAttributeAiInsightUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributePort loadAttributePort;
    private final LoadAttributeInsightPort loadAttributeInsightPort;
    private final UpdateAttributeInsightPort updateAttributeInsightPort;
    private final CreateAttributeAiInsightHelper createAttributeAiInsightHelper;
    private final CreateAttributeInsightPort createAttributeInsightPort;

    @SneakyThrows
    @Override
    public Result createAiInsight(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var attribute = loadAttributePort.load(param.getAttributeId(), assessmentResult.getKitVersionId());
        var attributeInsight = loadAttributeInsightPort.load(assessmentResult.getId(), attribute.getId());

        if (attributeInsight.isPresent() && isInsightValid(attributeInsight.get(), assessmentResult)) {
            updateAttributeInsightPort.updateAiInsightTime(toUpdateTimeParam(assessmentResult.getId(), attribute.getId()));
            return new Result(attributeInsight.get().getAiInsight());
        }
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());
        var attributeAiInsight = createAttributeAiInsightHelper.createAttributeAiInsight(new AttributeInsightParam(assessmentResult,
            param.getAttributeId(),
            locale));

        if (attributeInsight.isPresent())
            updateAttributeInsightPort.updateAiInsight(toUpdateParam(attributeAiInsight));
        else
            createAttributeInsightPort.persist(attributeAiInsight);

        return new Result(attributeAiInsight.getAiInsight());
    }

    private boolean isInsightValid(AttributeInsight attributeInsight, AssessmentResult assessmentResult) {
        return attributeInsight.getAiInsightTime() != null &&
            assessmentResult.getLastCalculationTime().isBefore(attributeInsight.getAiInsightTime());
    }

    private UpdateAttributeInsightPort.AiTimeParam toUpdateTimeParam(UUID assessmentResultId, long attributeId) {
        return new UpdateAttributeInsightPort.AiTimeParam(assessmentResultId,
            attributeId,
            LocalDateTime.now(),
            LocalDateTime.now());
    }

    private UpdateAttributeInsightPort.AiParam toUpdateParam(AttributeInsight attributeAiInsight) {
        return new UpdateAttributeInsightPort.AiParam(attributeAiInsight.getAssessmentResultId(),
            attributeAiInsight.getAttributeId(),
            attributeAiInsight.getAiInsight(),
            LocalDateTime.now(),
            attributeAiInsight.getAiInputPath(),
            false,
            LocalDateTime.now());
    }
}
