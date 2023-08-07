package org.flickit.flickitassessmentcore.domain.calculate;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class Answer {

    UUID id;

    @Nullable
    AnswerOption selectedOption;

    @Nullable
    public AnswerOptionImpact findImpactByMaturityLevel(MaturityLevel maturityLevel) {
        if (selectedOption == null)
            return null;
        return selectedOption.getImpacts().stream()
            .filter(i -> i.getQuestionImpact().getMaturityLevelId() == maturityLevel.getId())
            .findAny()
            .orElse(null);
    }
}
