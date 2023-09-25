package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Answer;
import org.flickit.flickitassessmentcore.application.domain.AnswerOption;

import java.util.List;
import java.util.UUID;

public class AnswerMother {

    public static Answer answer(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option);
    }

    public static Answer fullScoreOnLevels23() {
        return new Answer(UUID.randomUUID(), AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwo(1),
            AnswerOptionImpactMother.onLevelThree(1))));
    }

    public static Answer fullScoreOnLevels24() {
        return new Answer(UUID.randomUUID(), AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwo(1),
            AnswerOptionImpactMother.onLevelFour(1))));
    }

    public static Answer fullScoreOnLevels34() {
        return new Answer(UUID.randomUUID(), AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelThree(1),
            AnswerOptionImpactMother.onLevelFour(1))));
    }

    public static Answer fullScoreOnLevels45() {
        return new Answer(UUID.randomUUID(), AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFour(1),
            AnswerOptionImpactMother.onLevelFive(1))));
    }
}
