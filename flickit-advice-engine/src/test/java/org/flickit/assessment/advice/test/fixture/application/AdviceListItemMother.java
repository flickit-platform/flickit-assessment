package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;

import java.util.Collections;

public class AdviceListItemMother {

    public static AdviceListItem createSimpleAdviceListItem() {
        return new AdviceListItem(new AdviceQuestion(0L, "title", 1),
            new AdviceOption(1, "answeredOption"),
            new AdviceOption(2, "recommendedOption"),
            0.5,
            Collections.emptyList(),
            new AdviceQuestionnaire(3L, "Questionnaire title"));
    }
}
