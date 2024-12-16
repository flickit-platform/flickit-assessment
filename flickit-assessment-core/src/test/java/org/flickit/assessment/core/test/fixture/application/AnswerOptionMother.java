package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AnswerOption;

public class AnswerOptionMother {

    private static long id = 134L;

    public static AnswerOption optionOne() {
        return new AnswerOption(id++, 1, "one", 0.0);
    }

    public static AnswerOption withValue(double value) {
        return new AnswerOption(id++, 1, "one", value);
    }

    public static AnswerOption optionFour() {
        return new AnswerOption(id++, 4, "four", 1.0);
    }
}
