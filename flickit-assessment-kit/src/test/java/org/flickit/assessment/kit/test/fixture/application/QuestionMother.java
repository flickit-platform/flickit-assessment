package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Question;

import java.time.LocalDateTime;
import java.util.List;

import static org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother.createAnswerOption;

public class QuestionMother {

    private static Long id = 134L;
    private static int index = 1;

    public static Question createQuestion(String code, String title, int index, String hint, boolean mayNotBeApplicable, boolean advisable, Long questionnaireId) {
        return new Question(
            id++,
            code,
            title,
            index,
            hint,
            mayNotBeApplicable,
            advisable,
            questionnaireId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static Question createQuestion() {
        return new Question(
            id++,
            "code",
            "title",
            index++,
            "hint",
            true,
            true,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static Question createQuestionWithOptions() {
        Question question = new Question(
            id++,
            "code",
            "title",
            index++,
            "hint",
            true,
            true,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        var answerOption1 = createAnswerOption(question.getId(), "1st option", 0);
        var answerOption2 = createAnswerOption(question.getId(), "2nd option", 1);
        var answerOption3 = createAnswerOption(question.getId(), "3rd option", 2);

        question.setOptions(List.of(
            answerOption1,
            answerOption2,
            answerOption3
        ));
        return question;
    }
}
