package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;
import org.flickit.assessment.advice.application.domain.advice.QuestionRecommendation;

import java.util.Collections;

public class QuestionRecommendationMother {

    public static QuestionRecommendation createSimpleAdviceListItem() {
        return new QuestionRecommendation(new AdviceQuestion(0L, "title", 1),
            new AdviceOption(1, "answeredOption"),
            new AdviceOption(2, "recommendedOption"),
            0.5,
            Collections.emptyList(),
            new AdviceQuestionnaire(3L, "Questionnaire title"));
    }
}
