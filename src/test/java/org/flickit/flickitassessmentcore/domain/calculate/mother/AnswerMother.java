package org.flickit.flickitassessmentcore.domain.calculate.mother;

import org.flickit.flickitassessmentcore.domain.calculate.Answer;
import org.flickit.flickitassessmentcore.domain.calculate.AnswerOption;

import java.util.List;

public class AnswerMother {

    public static Answer answer(AnswerOption option) {
        return Answer.builder()
            .selectedOption(option)
            .build();
    }

    public static Answer fullScoreOnLevels23() {
        return Answer.builder()
            .selectedOption(AnswerOptionMother.withNoImpacts()
                .impacts(List.of(AnswerOptionImpactMother.onLevelTwo(1), AnswerOptionImpactMother.onLevelThree(1)))
                .build())
            .build();
    }

    public static Answer fullScoreOnLevels24() {
        return Answer.builder()
            .selectedOption(AnswerOptionMother.withNoImpacts()
                .impacts(List.of(AnswerOptionImpactMother.onLevelTwo(1), AnswerOptionImpactMother.onLevelFour(1)))
                .build())
            .build();
    }

    public static Answer fullScoreOnLevels34() {
        return Answer.builder()
            .selectedOption(AnswerOptionMother.withNoImpacts()
                .impacts(List.of(AnswerOptionImpactMother.onLevelThree(1), AnswerOptionImpactMother.onLevelFour(1)))
                .build())
            .build();
    }

    public static Answer fullScoreOnLevels45() {
        return Answer.builder()
            .selectedOption(AnswerOptionMother.withNoImpacts()
                .impacts(List.of(AnswerOptionImpactMother.onLevelFour(1), AnswerOptionImpactMother.onLevelFive(1)))
                .build())
            .build();
    }
}
