package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;
import org.flickit.assessment.kit.application.domain.AnswerRange;

import java.util.List;
import java.util.Map;

import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;
import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createSimpleAnswerOption;

public class AnswerRangeMother {

    private static Long id = 1L;
    private static int optionIndex = 1;

    public static AnswerRange createReusableAnswerRangeWithTwoOptions() {
        return new AnswerRange(
            id++,
            "title" + id,
            "title" + id,
            true,
            List.of(createSimpleAnswerOption(), createSimpleAnswerOption()),
            Map.of(KitLanguage.EN, new AnswerRangeTranslation("title" + id))
        );
    }

    public static AnswerRange createReusableAnswerRangeWithTwoOptions(long answerRangeId) {
        return new AnswerRange(
            answerRangeId,
            "title" + answerRangeId,
            "title" + answerRangeId,
            true,
            List.of(createAnswerOption(answerRangeId, "title" + answerRangeId, optionIndex++),
                createAnswerOption(answerRangeId, "title" + answerRangeId, optionIndex++))
        );
    }

    public static AnswerRange createAnswerRangeWithFourOptions() {
        return new AnswerRange(
            id++,
            "title" + id,
            "title" + id,
            true,
            List.of(createSimpleAnswerOption(), createSimpleAnswerOption(), createSimpleAnswerOption(), createSimpleAnswerOption())
        );
    }

    public static AnswerRange createNonReusableAnswerRangeWithTwoOptions() {
        return new AnswerRange(
            id++,
            "title" + id,
            "title" + id,
            false,
            List.of(createSimpleAnswerOption(), createSimpleAnswerOption())
        );
    }

    public static AnswerRange createAnswerRangeWithNoOptions(long id, boolean reusable) {
        return new AnswerRange(
            id,
            "title" + id,
            "title" + id,
            reusable,
            null
        );
    }

    public static AnswerRange createReusableAnswerRangeWithNoOptions() {
        return createAnswerRangeWithNoOptions(id++, true);
    }
}
