package org.flickit.flickitassessmentcore.domain.calculate;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Answer {

    private final UUID id;

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
