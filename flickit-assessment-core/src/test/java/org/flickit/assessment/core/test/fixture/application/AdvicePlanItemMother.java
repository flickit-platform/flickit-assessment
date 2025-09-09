package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.common.application.domain.advice.AdviceOption;
import org.flickit.assessment.common.application.domain.advice.AdvicePlanItem;
import org.flickit.assessment.common.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.common.application.domain.advice.AdviceQuestionnaire;

import java.util.Collections;

public class AdvicePlanItemMother {

    public static AdvicePlanItem createSimpleAdvicePlanItem() {
        return new AdvicePlanItem(new AdviceQuestion(0L, "title", 1),
            new AdviceOption(1, "answeredOption"),
            new AdviceOption(2, "recommendedOption"),
            Collections.emptyList(),
            new AdviceQuestionnaire(3L, "Questionnaire title"));
    }
}
