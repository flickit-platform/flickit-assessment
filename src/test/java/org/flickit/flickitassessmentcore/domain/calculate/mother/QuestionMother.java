package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.Question;
import org.flickit.flickitassessmentcore.domain.calculate.QuestionImpact;

import java.util.List;

import static org.flickit.flickitassessmentcore.domain.calculate.mother.QuestionImpactMother.*;

public class QuestionMother {

    private static long id = 134L;

    public static Question withImpacts(List<QuestionImpact> impacts) {
        return Question.builder()
            .id(id++)
            .impacts(impacts)
            .build();
    }

    public static Question withImpactsOnLevel23() {
        return Question.builder()
            .id(id++)
            .impacts(List.of(onLevelTwo(1), onLevelThree(1)))
            .build();
    }

    public static Question withImpactsOnLevel24() {
        return Question.builder()
            .id(id++)
            .impacts(List.of(onLevelTwo(1), onLevelFour(1)))
            .build();
    }

    public static Question withImpactsOnLevel34() {
        return Question.builder()
            .id(id++)
            .impacts(List.of(onLevelThree(1), onLevelFour(1)))
            .build();
    }

    public static Question withImpactsOnLevel45() {
        return Question.builder()
            .id(id++)
            .impacts(List.of(onLevelFour(1), onLevelFive(1)))
            .build();
    }

}
