package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;

import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AnswerOptionImpactMother.onLevelFour;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionImpactMother.onLevelThree;

public class AnswerOptionMother {

    private static long id = 134L;

    public static AnswerOption withImpacts(List<AnswerOptionImpact> impacts) {
        return new AnswerOption(id, null, null, 123L, impacts);
    }

    public static AnswerOption optionOne() {
        return new AnswerOption(id++, 1, "one", 123L, List.of(
            onLevelThree(0),
            onLevelFour(0)));
    }

    public static AnswerOption optionTwo() {
        return new AnswerOption(id++, 2, "two", 123L, List.of(
            onLevelThree(0.5),
            onLevelFour(0)));
    }

    public static AnswerOption optionThree() {
        return new AnswerOption(id++, 3, "three", 123L, List.of(
            onLevelThree(1),
            onLevelFour(0)));
    }

    public static AnswerOption optionFour() {
        return new AnswerOption(id++, 4, "four", 123L, List.of(
            onLevelThree(1),
            onLevelFour(1)));
    }
}
