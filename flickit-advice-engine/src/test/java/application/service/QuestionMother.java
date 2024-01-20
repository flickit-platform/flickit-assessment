package application.service;

import application.domain.Question;
import application.domain.Target;

import static application.service.OptionMother.createOptions;

public class QuestionMother {

    private static long id = 0L;

    public static Question createQuestionWithTargetAndOptionIndexes(Target target, int currentOptionIndex, int recommendedOptionIndex) {
        Question question = new Question(id++, 10, createOptions(target), currentOptionIndex);
        question.setRecommendedOptionIndex(recommendedOptionIndex);
        return question;
    }
}
