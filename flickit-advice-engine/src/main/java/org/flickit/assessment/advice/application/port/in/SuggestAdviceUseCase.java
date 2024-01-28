package org.flickit.assessment.advice.application.port.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SuggestAdviceUseCase {

    Result suggestAdvice(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = "suggest-advice.assessmentId.notNull")
        UUID assessmentId;

        @Size(min = 1, message = "suggest-advice.targets.size.min")
        Map<Long, Long> targets;

        public Param(UUID assessmentId, Map<Long, Long> targets) {
            this.assessmentId = assessmentId;
            this.targets = targets;
            this.validateSelf();
        }
    }

    record Result(
        List<QuestionListItem> questions
    ) {
    }

    record QuestionListItem(
        Long id,
        String title,
        List<OptionListItem> options,
        Long currentOptionIndex,
        Long recommendedOptionIndex,
        Double benefit,
        List<AttributeListItem> attributes,
        Questionnaire questionnaire
    ) {
    }

    record OptionListItem(
        Long index,
        String caption
    ) {
    }

    record AttributeListItem(
        Long id,
        String title
    ) {
    }

    record Questionnaire(
        Long id,
        String title
    ) {
    }
}
