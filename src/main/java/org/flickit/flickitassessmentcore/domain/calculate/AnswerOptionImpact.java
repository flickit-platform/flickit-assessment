package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerOptionImpact {

    private final long id;
    private final double value;
    private final QuestionImpact questionImpact;

    public Double calculateScore() {
        return questionImpact.getWeight() * value;
    }
}
