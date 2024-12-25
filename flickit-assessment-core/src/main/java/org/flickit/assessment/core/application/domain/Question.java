package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Question {

    private final long id;
    private final String title;
    private final Integer index;
    private final String hint;
    private final Boolean mayNotBeApplicable;
    private final List<QuestionImpact> impacts;
    private final Questionnaire questionnaire;
    @Setter
    private List<AnswerOption> options;

    public QuestionImpact findImpactByAttributeAndMaturityLevel(long attributeId, long maturityLevelId) {
        return impacts.stream()
            .filter(i -> i.getAttributeId() == attributeId && i.getMaturityLevelId() == maturityLevelId)
            .findAny()
            .orElse(null);
    }
}
