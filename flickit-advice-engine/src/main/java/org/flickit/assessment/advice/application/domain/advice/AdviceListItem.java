package org.flickit.assessment.advice.application.domain.advice;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record AdviceListItem(
    AdviceQuestion question,
    AdviceOptionListItem answeredOption,
    AdviceOptionListItem recommendedOption,
    @JsonIgnore double benefit,
    List<AttributeListItem> attributes,
    QuestionnaireListItem questionnaire
) {
}
