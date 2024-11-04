package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;

import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AnswerOptionImpactMother.onLevelFourOfAttributeId;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionImpactMother.onLevelThreeOfAttributeId;

public class AnswerOptionMother {

    private static long id = 134L;

    public static AnswerOption withImpacts(List<AnswerOptionImpact> impacts) {
        return new AnswerOption(id, null, null, impacts);
    }

    public static AnswerOption optionOne() {
        long attributeId = 1533;
        return new AnswerOption(id++, 1, "one", List.of(
            onLevelThreeOfAttributeId(0, attributeId),
            onLevelFourOfAttributeId(0, attributeId)));
    }
    public static AnswerOption optionOne(long attributeId) {
        return new AnswerOption(id++, 1, "one", List.of(
            onLevelThreeOfAttributeId(0, attributeId),
            onLevelFourOfAttributeId(0, attributeId)));
    }

    public static AnswerOption optionTwo() {
        long attributeId = 1533;
        return new AnswerOption(id++, 2, "two", List.of(
            onLevelThreeOfAttributeId(0.5, attributeId),
            onLevelFourOfAttributeId(0, attributeId)));
    }

    public static AnswerOption optionTwo(long attributeId) {
        return new AnswerOption(id++, 2, "two", List.of(
            onLevelThreeOfAttributeId(0.5, attributeId),
            onLevelFourOfAttributeId(0, attributeId)));
    }

    public static AnswerOption optionThree(long attributeId) {
        return new AnswerOption(id++, 3, "three", List.of(
            onLevelThreeOfAttributeId(1, attributeId),
            onLevelFourOfAttributeId(0, attributeId)));
    }

    public static AnswerOption optionFour(long attributeId) {
        return new AnswerOption(id++, 4, "four", List.of(
            onLevelThreeOfAttributeId(1, attributeId),
            onLevelFourOfAttributeId(1, attributeId)));
    }
}
