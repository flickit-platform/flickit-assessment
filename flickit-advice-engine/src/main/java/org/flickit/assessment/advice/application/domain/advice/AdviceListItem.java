package org.flickit.assessment.advice.application.domain.advice;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record AdviceListItem(
    AdviceQuestion question,
    AdviceOption answeredOption,
    AdviceOption recommendedOption,
    @JsonIgnore double benefit,
    List<AdviceAttribute> attributes,
    AdviceQuestionnaire questionnaire
) {
}
