package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.UUID;

public class AnswerMother {

    public static Answer answer(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, 1L, ConfidenceLevel.getDefault().getId(), Boolean.FALSE);
    }

    public static Answer fullScore(long questionId) {
        AnswerOption selectedOption = AnswerOptionMother.optionFour();
        return new Answer(UUID.randomUUID(),
            selectedOption,
            questionId,
            ConfidenceLevel.COMPLETELY_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer partialScore(long questionId, double value) {
        AnswerOption selectedOption = AnswerOptionMother.withValue(value);
        return new Answer(UUID.randomUUID(),
            selectedOption,
            questionId,
            ConfidenceLevel.COMPLETELY_UNSURE.getId(),
            Boolean.FALSE);
    }

    public static Answer noScore(long questionId) {
        AnswerOption selectedOption = AnswerOptionMother.optionOne();
        return new Answer(UUID.randomUUID(),
            selectedOption,
            questionId,
            ConfidenceLevel.FAIRLY_SURE.getId(),
            Boolean.FALSE);
    }

    public static Answer answerWithNullNotApplicable(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, 1L, ConfidenceLevel.getDefault().getId(), null);
    }

    public static Answer answerWithNotApplicableFalse(AnswerOption option) {
        return new Answer(UUID.randomUUID(), option, 1L, ConfidenceLevel.getDefault().getId(), Boolean.FALSE);
    }

    public static Answer answerWithNotApplicableTrue(AnswerOption option) {
        Integer confidenceLevelId = ConfidenceLevel.getDefault().getId();
        return new Answer(UUID.randomUUID(), option, 1L, confidenceLevelId, Boolean.TRUE);
    }

    public static Answer answerWithQuestionIdAndNotApplicableTrue(long questionId) {
        return new Answer(UUID.randomUUID(), null, questionId, ConfidenceLevel.getDefault().getId(), Boolean.TRUE);
    }

    public static Answer answerWithConfidenceLevel(int confidenceLevelId, Long questionId) {
        AnswerOption selectedOption = AnswerOptionMother.optionFour();
        return new Answer(UUID.randomUUID(),
            selectedOption,
            questionId,
            confidenceLevelId,
            Boolean.FALSE);
    }
}
