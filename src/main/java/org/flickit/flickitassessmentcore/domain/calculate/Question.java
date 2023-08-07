package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Question {

    long id;
    List<QuestionImpact> impacts;

    public QuestionImpact findImpactByMaturityLevel(MaturityLevel maturityLevel) {
        return impacts.stream()
            .filter(i -> i.getMaturityLevelId() == maturityLevel.getId())
            .findAny()
            .orElse(null);
    }

}
