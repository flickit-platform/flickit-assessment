package org.flickit.assessment.advice.application.domain.advice;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record QuestionListItem(
    long id,
    String title,
    int index,
    Integer currentOptionIndex,
    int recommendedOptionIndex,
    @JsonIgnore double benefit,
    List<OptionListItem> options,
    List<AttributeListItem> attributes,
    QuestionnaireListItem questionnaire
) {
}
