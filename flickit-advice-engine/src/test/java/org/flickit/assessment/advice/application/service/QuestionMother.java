package org.flickit.assessment.advice.application.service;

import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.Target;

import static org.flickit.assessment.advice.application.service.OptionMother.createOptions;

public class QuestionMother {

    private static long id = 0L;

    public static Question createQuestionWithTargetAndOptionIndexes(Target target, int currentOptionIndex, int recommendedOptionIndex) {
        Question question = new Question(id++, 10, createOptions(target), currentOptionIndex);
        question.setRecommendedOptionIndex(recommendedOptionIndex);
        return question;
    }
}
