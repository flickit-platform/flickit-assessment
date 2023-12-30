package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.List;
import java.util.UUID;

public class AnswerMother {

    public static Answer answer(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, option.getQuestionId(), ConfidenceLevel.getDefault().getId(), Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels23() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwo(1),
            AnswerOptionImpactMother.onLevelThree(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            ConfidenceLevel.COMPLETELY_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels24() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelTwo(1),
            AnswerOptionImpactMother.onLevelFour(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            ConfidenceLevel.SOMEWHAT_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer noScoreOnLevel4() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFour(0)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevel4AndNoScoreOnLevel5() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFour(1),
            AnswerOptionImpactMother.onLevelFive(0)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels34() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelThree(1),
            AnswerOptionImpactMother.onLevelFour(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer fullScoreOnLevels45() {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFour(1),
            AnswerOptionImpactMother.onLevelFive(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            selectedOption.getQuestionId(),
            ConfidenceLevel.COMPLETELY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer answerWithNullNotApplicable(AnswerOption option) {
        Long questionId = option != null ? option.getQuestionId() : 1L;
        return new Answer(UUID.randomUUID(), option, questionId, ConfidenceLevel.getDefault().getId(), null);
    }

    public static Answer answerWithNotApplicableFalse(AnswerOption option) {
        Long questionId = option != null ? option.getQuestionId() : 1L;
        return new Answer(UUID.randomUUID(), option, questionId, ConfidenceLevel.getDefault().getId(), Boolean.FALSE);
    }

    public static Answer answerWithNotApplicableTrue(AnswerOption option) {
        Long questionId = option != null ? option.getQuestionId() : 1L;
        Integer confidenceLevelId = ConfidenceLevel.getDefault().getId();
        return new Answer(UUID.randomUUID(), option, questionId, confidenceLevelId, Boolean.TRUE);
    }

    public static Answer answerWithQuestionIdAndNotApplicableTrue(long questionId) {
        return new Answer(UUID.randomUUID(), null, questionId, ConfidenceLevel.getDefault().getId(), Boolean.TRUE);
    }

    public static Answer answerWithConfidenceLevel(int confidenceLevelId, Long questionId) {
        AnswerOption selectedOption = AnswerOptionMother.withImpacts(List.of(
            AnswerOptionImpactMother.onLevelFive(1)));
        return new Answer(UUID.randomUUID(),
            selectedOption,
            questionId,
            confidenceLevelId,
            Boolean.FALSE);
    }

}
