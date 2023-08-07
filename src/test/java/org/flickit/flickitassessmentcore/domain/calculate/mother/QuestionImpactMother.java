package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.QuestionImpact;

import static org.flickit.flickitassessmentcore.domain.calculate.mother.MaturityLevelMother.*;

public class QuestionImpactMother {

    public static QuestionImpact onLevelOne(int weight) {
        return QuestionImpact.builder()
            .maturityLevelId(levelOne().getId())
            .weight(weight)
            .build();
    }

    public static QuestionImpact onLevelTwo(int weight) {
        return QuestionImpact.builder()
            .maturityLevelId(levelTwo().getId())
            .weight(weight)
            .build();
    }

    public static QuestionImpact onLevelThree(int weight) {
        return QuestionImpact.builder()
            .maturityLevelId(levelThree().getId())
            .weight(weight)
            .build();
    }

    public static QuestionImpact onLevelFour(int weight) {
        return QuestionImpact.builder()
            .maturityLevelId(levelFour().getId())
            .weight(weight)
            .build();
    }

    public static QuestionImpact onLevelFive(int weight) {
        return QuestionImpact.builder()
            .maturityLevelId(levelFive().getId())
            .weight(weight)
            .build();
    }
}
