package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Question;

import static org.flickit.assessment.advice.test.fixture.application.OptionMother.createOptions;

public class QuestionMother {

    private static long id = 0L;

    public static Question createQuestionWithTargetAndOptionIndexes(AttributeLevelScore attributeLevelScore, Integer currentOptionIndex, Integer recommendedOptionIndex) {
        Question question = new Question(id++, 10, createOptions(attributeLevelScore), currentOptionIndex);
        question.setRecommendedOptionIndex(recommendedOptionIndex);
        return question;
    }

    public static Question createQuestionWithTargetAndCurrentOption(AttributeLevelScore attributeLevelScore, Integer currentOptionIndex) {
        return new Question(id++, 10, createOptions(attributeLevelScore), currentOptionIndex);
    }
}
