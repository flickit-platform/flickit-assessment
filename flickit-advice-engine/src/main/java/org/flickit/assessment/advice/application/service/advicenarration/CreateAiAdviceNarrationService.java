package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAiAdviceNarrationService implements CreateAiAdviceNarrationUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final CreateAdviceNarrationPort createAdviceNarrationPort;
    private final AppAiProperties appAiProperties;
    private final OpenAiProperties openAiProperties;
    private final CallAiPromptPort callAiPromptPort;

    @Override
    public Result createAiAdviceNarration(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (!appAiProperties.isEnabled())
            return new Result(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED));

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND));

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());

        var attributeLevelTargets = filterValidAttributeLevelTargets(param.getAssessmentId(), param.getAttributeLevelTargets());

        var prompt = buildPrompt(param.getAdviceListItems(), attributeLevelTargets, assessmentResult.getKitVersionId());
        var aiNarration = callAiPromptPort.call(prompt);

        if (adviceNarration.isPresent()) {
            UUID narrationId = adviceNarration.get().getId();
            UUID assessmentResultId = assessmentResult.getId();
            handleExistingAdviceNarration(narrationId, assessmentResultId, aiNarration, param.getCurrentUserId());
        } else {
            UUID assessmentResultId = assessmentResult.getId();
            handleNewAdviceNarration(assessmentResultId, aiNarration, param.getCurrentUserId());
        }
        return new Result(aiNarration);
    }

    private List<AttributeLevelTarget> filterValidAttributeLevelTargets(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var attributeCurrentAndTargetLevelIndexes = loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(assessmentId, attributeLevelTargets);
        var validAttributeIds = attributeCurrentAndTargetLevelIndexes.stream()
            .filter(a -> a.targetMaturityLevelIndex() > a.currentMaturityLevelIndex())
            .map(LoadAttributeCurrentAndTargetLevelIndexPort.Result::attributeId)
            .collect(Collectors.toSet());
        if (validAttributeIds.isEmpty())
            throw new ValidationException(CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);

        return attributeLevelTargets.stream()
            .filter(a -> validAttributeIds.contains(a.getAttributeId()))
            .toList();
    }

    private Prompt buildPrompt(List<AdviceListItem> adviceItems, List<AttributeLevelTarget> targets, long kitVersionId) {
        var maturityLevelsMap = loadMaturityLevelsPort.loadAll(kitVersionId).stream()
            .collect(Collectors.toMap(MaturityLevel::getId, MaturityLevel::getTitle));

        List<Long> targetAttributeIds = targets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .toList();
        var attributesMap = loadAttributesPort.loadByIdsAndKitVersionId(targetAttributeIds, kitVersionId).stream()
            .collect(Collectors.toMap(Attribute::getId, Attribute::getTitle));

        var promptAdviceItems = adviceItems.stream()
            .map(a -> new AdviceItem(a.question().title(),
                a.answeredOption() != null ? a.answeredOption().title() : null,
                a.recommendedOption().title()))
            .toList();

        List<TargetAttribute> targetAttributes = targets.stream()
            .map(target -> new TargetAttribute(
                attributesMap.get(target.getAttributeId()),
                maturityLevelsMap.getOrDefault(target.getMaturityLevelId(), "Unknown")))
            .toList();

        return openAiProperties.createAiAdviceNarrationPrompt(promptAdviceItems.toString(), targetAttributes.toString());
    }

    record AdviceItem(String question, String currentOption, String recommendedOption) {
    }

    record TargetAttribute(String attribute, String maturityLevel) {
    }

    private void handleExistingAdviceNarration(UUID adviceId, UUID assessmentResultId, String aiNarration, UUID createdBy) {
        createAdviceNarrationPort.persist(toAdviceNarration(adviceId, assessmentResultId, aiNarration, createdBy));
    }

    private void handleNewAdviceNarration(UUID assessmentResultId, String aiNarration, UUID createdBy) {
        createAdviceNarrationPort.persist(toAdviceNarration(null, assessmentResultId, aiNarration, createdBy));
    }

    AdviceNarration toAdviceNarration(UUID adviceId, UUID assessmentResultId, String aiNarration, UUID createdBy) {
        return new AdviceNarration(adviceId, assessmentResultId,
            aiNarration,
            null,
            LocalDateTime.now(),
            null,
            createdBy);
    }
}
