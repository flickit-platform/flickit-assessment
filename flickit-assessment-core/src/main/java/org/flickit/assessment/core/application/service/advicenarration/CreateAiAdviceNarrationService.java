package org.flickit.assessment.core.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.advice.AdvicePlanItem;
import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.core.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAiAdviceNarrationService implements CreateAiAdviceNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAttributeValuePort loadAttributeValuePort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final LoadAssessmentPort loadAssessmentPort;
    private final CreateAdviceNarrationPort createAdviceNarrationPort;
    private final CallAiPromptPort callAiPromptPort;
    private final CreateAdviceItemPort createAdviceItemPort;
    private final AppAiProperties appAiProperties;
    private final UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Override
    public Result createAiAdviceNarration(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!appAiProperties.isEnabled())
            return new Result(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED));

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var attributeLevelTargets = filterValidAttributeLevelTargets(param.getAssessmentId(), param.getAttributeLevelTargets());

        var prompt = createPrompt(param.getAdviceListItems(), attributeLevelTargets, assessmentResult, param.getAssessmentId());
        AdviceDto aiAdvice = callAiPromptPort.call(prompt, AdviceDto.class);

        createAdviceItems(aiAdvice.adviceItems, assessmentResult.getId());

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());
        if (adviceNarration.isPresent()) {
            UUID narrationId = adviceNarration.get().getId();
            var updateParam = toAiNarrationParam(narrationId, aiAdvice.narration);
            updateAdviceNarrationPort.updateAiNarration(updateParam);
        } else {
            UUID assessmentResultId = assessmentResult.getId();
            createAdviceNarrationPort.persist(toCreateAiAdviceNarrationParam(aiAdvice.narration()), assessmentResultId);
        }
        return new Result(aiAdvice.narration());
    }

    private List<AttributeLevelTarget> filterValidAttributeLevelTargets(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var attributeCurrentAndTargetLevelIndexes = loadAttributeValuePort.loadCurrentAndTargetLevelIndices(assessmentId, attributeLevelTargets);
        var validAttributeIds = attributeCurrentAndTargetLevelIndexes.stream()
            .filter(a -> a.targetMaturityLevelIndex() > a.currentMaturityLevelIndex())
            .map(LoadAttributeValuePort.AttributeLevelIndex::attributeId)
            .collect(Collectors.toSet());
        if (validAttributeIds.isEmpty())
            throw new ValidationException(CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);

        return attributeLevelTargets.stream()
            .filter(a -> validAttributeIds.contains(a.getAttributeId()))
            .toList();
    }

    private Prompt createPrompt(List<AdvicePlanItem> adviceItems, List<AttributeLevelTarget> targets, AssessmentResult assessmentResult, UUID assessmentId) {
        var assessment = loadAssessmentPort.loadById(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var assessmentTitle = assessment.getShortTitle() != null ? assessment.getShortTitle() : assessment.getTitle();

        var maturityLevelsMap = loadMaturityLevelsPort.loadAllByAssessment(assessmentId).stream()
            .collect(Collectors.toMap(MaturityLevel::getId, MaturityLevel::getTitle));

        List<Long> targetAttributeIds = targets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .toList();
        var attributesMap = loadAttributesPort.loadByIdsAndAssessmentId(targetAttributeIds, assessmentId).stream()
            .collect(Collectors.toMap(Attribute::getId, Attribute::getTitle));

        var adviceRecommendations = adviceItems.stream()
            .map(a -> new AdviceRecommendation(a.question().title(),
                a.answeredOption() != null ? a.answeredOption().title() : null,
                a.recommendedOption().title()))
            .toList();

        List<TargetAttribute> targetAttributes = targets.stream()
            .map(target -> new TargetAttribute(
                attributesMap.get(target.getAttributeId()),
                maturityLevelsMap.getOrDefault(target.getMaturityLevelId(), "Unknown")))
            .toList();

        return new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("assessmentTitle", assessmentTitle,
                "attributeTargets", targetAttributes,
                "adviceRecommendations", adviceRecommendations,
                "language", assessmentResult.getLanguage().getTitle()))
            .create();
    }

    private UpdateAdviceNarrationPort.AiNarrationParam toAiNarrationParam(UUID narrationId, String narration) {
        return new UpdateAdviceNarrationPort.AiNarrationParam(narrationId,
            narration,
            false,
            LocalDateTime.now());
    }

    void createAdviceItems(List<AdviceDto.AdviceItemDto> adviceItems, UUID assessmentResultId) {
        var createAdviceItemsParam = adviceItems.stream()
            .map(AdviceDto.AdviceItemDto::toCreateParam)
            .toList();
        createAdviceItemPort.persistAll(createAdviceItemsParam, assessmentResultId);
    }

    record AdviceDto(String narration, List<AdviceItemDto> adviceItems) {

        record AdviceItemDto(String title, String description, int cost, int priority, int impact) {

            CreateAdviceItemPort.Param toCreateParam() {
                return new CreateAdviceItemPort.Param(
                    title,
                    description,
                    CostLevel.valueOfById(cost),
                    PriorityLevel.valueOfById(priority),
                    ImpactLevel.valueOfById(impact),
                    LocalDateTime.now(),
                    null);
            }
        }
    }

    record AdviceRecommendation(String question, String currentOption, String recommendedOption) {
    }

    record TargetAttribute(String attribute, String targetMaturityLevel) {
    }

    CreateAdviceNarrationPort.Param toCreateAiAdviceNarrationParam(String aiNarration) {
        return new CreateAdviceNarrationPort.Param(aiNarration,
            null,
            false,
            LocalDateTime.now(),
            null,
            null);
    }
}
