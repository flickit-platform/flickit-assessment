package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Question {

    private final long id;
    private final List<QuestionImpact> impacts;

    public QuestionImpact findImpactByMaturityLevel(MaturityLevel maturityLevel) {
        return impacts.stream()
            .filter(i -> i.getMaturityLevelId() == maturityLevel.getId())
            .findAny()
            .orElse(null);
    }
}
