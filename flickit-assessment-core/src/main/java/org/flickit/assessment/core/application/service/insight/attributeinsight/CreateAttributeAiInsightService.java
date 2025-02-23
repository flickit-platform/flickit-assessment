package org.flickit.assessment.core.application.service.insight.attributeinsight;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.insight.attributeinsight.CreateAttributeAiInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.insight.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
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

        var maturityLevels = loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId());
        var progress = getAssessmentProgressPort.getProgress(param.getAssessmentId());
        var attributeAiInsight = createAttributeAiInsightHelper.createAttributeAiInsight(toCreateAiInsightParam(param,
            assessmentResult,
            maturityLevels,
            progress,
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

    private CreateAttributeAiInsightHelper.Param toCreateAiInsightParam(Param param,
                                                                        AssessmentResult assessmentResult,
                                                                        List<MaturityLevel> maturityLevels,
                                                                        GetAssessmentProgressPort.Result progress,
                                                                        Locale locale) {
        return new CreateAttributeAiInsightHelper.Param(assessmentResult,
            param.getAttributeId(),
            maturityLevels,
            progress,
            locale);
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
