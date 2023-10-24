package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Answer;
import org.flickit.flickitassessmentcore.application.domain.AnswerOption;

import java.util.List;
import java.util.UUID;

public class AnswerMother {

    public static Answer answer(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, option.getQuestionId(), Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels23() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwo(1),
            AnswerOptionImpactMother.onLevelThree(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels24() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwo(1),
            AnswerOptionImpactMother.onLevelFour(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            Boolean.FALSE);
    }

    public static Answer noScoreOnLevel4() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFour(0)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            Boolean.FALSE);
    }

    public static Answer noScoreOnLevel5() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFive(0)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels34() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelThree(1),
            AnswerOptionImpactMother.onLevelFour(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels45() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFour(1),
            AnswerOptionImpactMother.onLevelFive(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            Boolean.FALSE);
    }

    public static Answer answer(AnswerOption option, Boolean isNotApplicable) {
        return new Answer(UUID.randomUUID(), option, option.getQuestionId(), isNotApplicable);
    }
}
