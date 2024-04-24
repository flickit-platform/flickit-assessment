package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Question;

import java.time.LocalDateTime;

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

    public static Question createQuestion(){
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
}
