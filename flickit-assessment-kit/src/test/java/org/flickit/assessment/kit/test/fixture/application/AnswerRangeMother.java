package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AnswerRange;


public class AnswerRangeMother {

    private static Long id = 1L;

    public static AnswerRange createAnswerRange() {
        return new AnswerRange(
            id++,
            "title" + id,
            null
        );
    }
}
