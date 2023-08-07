package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnswerOptionImpact {

    long id;
    double value;
    QuestionImpact questionImpact;

    public Double calculateScore() {
        return questionImpact.getWeight() * value;
    }
}
