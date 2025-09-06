package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.*;
import org.flickit.assessment.advice.application.domain.advice.QuestionRecommendation;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAiAdviceNarrationHelper {

    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadAdviceNarrationPort loadAdviceNarrationPort;
    private final CreateAdviceNarrationPort createAdviceNarrationPort;
    private final CallAiPromptPort callAiPromptPort;
    private final CreateAdviceItemPort createAdviceItemPort;
    private final AppAiProperties appAiProperties;
    private final UpdateAdviceNarrationPort updateAdviceNarrationPort;

    public String createAiAdviceNarration(AssessmentResult assessmentResult,
                                          List<QuestionRecommendation> questionRecommendations,
                                          List<AttributeLevelTarget> attributeLevelTargets) {
        if (!appAiProperties.isEnabled())
            return MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED);

        var prompt = createPrompt(questionRecommendations, attributeLevelTargets, assessmentResult.getAssessmentId(), assessmentResult.getLanguage());
        AdviceDto aiAdvice = callAiPromptPort.call(prompt, AdviceDto.class);

        var adviceItems = aiAdvice.adviceItems().stream()
            .map(AdviceDto.AdviceItemDto::toCreateParam)
            .toList();
        createAdviceItemPort.persistAll(adviceItems, assessmentResult.getId());

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());
        if (adviceNarration.isPresent()) {
            UUID narrationId = adviceNarration.get().getId();
            var updateParam = toAiNarrationParam(narrationId, aiAdvice.narration);
            updateAdviceNarrationPort.updateAiNarration(updateParam);
        } else {
            UUID assessmentResultId = assessmentResult.getId();
            createAdviceNarrationPort.persist(toAiAdviceNarration(assessmentResultId, aiAdvice.narration()));
        }
        return aiAdvice.narration();
    }

    private Prompt createPrompt(List<QuestionRecommendation> adviceItems, List<AttributeLevelTarget> targets, UUID assessmentId, KitLanguage kitLanguage) {
        var maturityLevelsMap = loadMaturityLevelsPort.loadAll(assessmentId).stream()
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
            Map.of("attributeTargets", targetAttributes,
                "adviceRecommendations", adviceRecommendations,
                "language", kitLanguage.getTitle()))
            .create();
    }

    private UpdateAdviceNarrationPort.AiNarrationParam toAiNarrationParam(UUID narrationId, String narration) {
        return new UpdateAdviceNarrationPort.AiNarrationParam(narrationId,
            narration,
            false,
            LocalDateTime.now());
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

    AdviceNarration toAiAdviceNarration(UUID assessmentResultId, String aiNarration) {
        return new AdviceNarration(null,
            assessmentResultId,
            aiNarration,
            null,
            false,
            LocalDateTime.now(),
            null,
            null);
    }
}
