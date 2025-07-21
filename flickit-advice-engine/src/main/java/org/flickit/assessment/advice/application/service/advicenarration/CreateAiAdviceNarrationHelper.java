package org.flickit.assessment.advice.application.service.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.*;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
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
                                          List<AdviceListItem> adviceListItems,
                                          List<AttributeLevelTarget> attributeLevelTargets) {
        if (!appAiProperties.isEnabled())
            return MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED);

        var adviceNarration = loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId());

        var prompt = createPrompt(adviceListItems, attributeLevelTargets, assessmentResult);
        AdviceDto aiAdvice = callAiPromptPort.call(prompt, AdviceDto.class);

        var adviceItems = aiAdvice.adviceItems().stream()
            .map(i -> i.toDomainModel(assessmentResult.getId()))
            .toList();
        createAdviceItemPort.persistAll(adviceItems);

        if (adviceNarration.isPresent()) {
            UUID narrationId = adviceNarration.get().getId();
            var updateParam = new UpdateAdviceNarrationPort.AiNarrationParam(narrationId, aiAdvice.narration(), LocalDateTime.now());
            updateAdviceNarrationPort.updateAiNarration(updateParam);
        } else {
            UUID assessmentResultId = assessmentResult.getId();
            createAdviceNarrationPort.persist(toAdviceNarration(assessmentResultId, aiAdvice.narration()));
        }
        return aiAdvice.narration();
    }

    private Prompt createPrompt(List<AdviceListItem> adviceItems, List<AttributeLevelTarget> targets, AssessmentResult assessmentResult) {
        var maturityLevelsMap = loadMaturityLevelsPort.loadAll(assessmentResult.getAssessmentId()).stream()
            .collect(Collectors.toMap(MaturityLevel::getId, MaturityLevel::getTitle));

        List<Long> targetAttributeIds = targets.stream()
            .map(AttributeLevelTarget::getAttributeId)
            .toList();
        var attributesMap = loadAttributesPort.loadByIdsAndAssessmentId(targetAttributeIds, assessmentResult.getAssessmentId()).stream()
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
                "language", assessmentResult.getLanguage().getTitle()))
            .create();
    }

    record AdviceDto(String narration, List<AdviceItemDto> adviceItems) {

        record AdviceItemDto(String title, String description, int cost, int priority, int impact) {

            AdviceItem toDomainModel(UUID assessmentResultId) {
                return new AdviceItem(null,
                    title,
                    assessmentResultId,
                    description,
                    CostLevel.valueOfById(cost),
                    PriorityLevel.valueOfById(priority),
                    ImpactLevel.valueOfById(impact),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null,
                    null);
            }
        }
    }

    record AdviceRecommendation(String question, String currentOption, String recommendedOption) {
    }

    record TargetAttribute(String attribute, String targetMaturityLevel) {
    }

    AdviceNarration toAdviceNarration(UUID assessmentResultId, String aiNarration) {
        return new AdviceNarration(null,
            assessmentResultId,
            aiNarration,
            null,
            LocalDateTime.now(),
            null,
            null);
    }
}
