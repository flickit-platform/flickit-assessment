package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AnswerRange;

import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createSimpleAnswerOption;

public class AnswerRangeMother {

    private static Long id = 1L;

    public static AnswerRange createAnswerRangeWithTwoOptions() {
        return new AnswerRange(
            id++,
            "title" + id,
            true,
            List.of(createSimpleAnswerOption(), createSimpleAnswerOption())
        );
    }

    public static AnswerRange createAnswerRangeWithFourOptions() {
        return new AnswerRange(
            id++,
            "title" + id,
            true,
            List.of(createSimpleAnswerOption(), createSimpleAnswerOption(), createSimpleAnswerOption(), createSimpleAnswerOption())
        );
    }

    public static AnswerRange createNonreusableAnswerRangeWithTwoOptions() {
        return new AnswerRange(
            id++,
            "title" + id,
            false,
            List.of(createSimpleAnswerOption(), createSimpleAnswerOption())
        );
    }

    public static AnswerRange createAnswerRangeWithIsReusable(boolean reusable) {
        return new AnswerRange(
            id++,
            "title" + id,
            reusable,
            null
        );
    }
}
