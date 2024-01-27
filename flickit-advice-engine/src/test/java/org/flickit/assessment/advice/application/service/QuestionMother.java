package org.flickit.assessment.advice.application.service;

import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Question;

import static org.flickit.assessment.advice.application.service.OptionMother.createOptions;

public class QuestionMother {

    private static long id = 0L;

    public static Question createQuestionWithTargetAndOptionIndexes(AttributeLevelScore attributeLevelScore, int currentOptionIndex, int recommendedOptionIndex) {
        Question question = new Question(id++, 10, createOptions(attributeLevelScore), currentOptionIndex);
        question.setRecommendedOptionIndex(recommendedOptionIndex);
        return question;
    }
}
