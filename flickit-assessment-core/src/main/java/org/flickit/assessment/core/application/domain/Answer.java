package org.flickit.assessment.core.application.domain;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Answer {

    private final UUID id;

    @Nullable
    private final AnswerOption selectedOption;

    private final Long questionId;

    private final Integer confidenceLevelId;

    private final Boolean isNotApplicable;

    @Nullable
    public AnswerOptionImpact findImpactByAttributeAndMaturityLevel(Attribute attribute, MaturityLevel maturityLevel) {
        if (selectedOption == null)
            return null;
        return selectedOption.getImpacts().stream()
            .filter(i -> i.getQuestionImpact().getAttributeId() == attribute.getId() &&
                i.getQuestionImpact().getMaturityLevelId() == maturityLevel.getId())
            .findAny()
            .orElse(null);
    }
}
