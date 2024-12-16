package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AnswerOption;

public class AnswerOptionMother {

    private static Long id = 134L;
    private static int index = 1;
    private static Long answerRangeId = 123L;
    private static double value = 0.1;

    public static AnswerOption createSimpleAnswerOption() {
        return new AnswerOption(id++,
            "title" + id,
            index++,
            answerRangeId++,
            value += 0.1);
    }

    public static AnswerOption createAnswerOption(long answerRangeId, String title, int index) {
        return new AnswerOption(
            id++,
            title,
            index,
            answerRangeId,
            value += 0.1);
    }

    public static AnswerOption optionOne() {
        return optionOne(111L);
    }

    public static AnswerOption optionOne(long answerRangeId) {
        return new AnswerOption(id++, "one", 1, answerRangeId, 0.0);
    }

    public static AnswerOption optionTwo() {
        return optionTwo(111L);
    }

    public static AnswerOption optionTwo(long answerRangeId) {
        return new AnswerOption(id++, "two", 2, answerRangeId, 1.0);
    }
}
