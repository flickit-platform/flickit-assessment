package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Question;

import java.time.LocalDateTime;

public class QuestionMother {

    private static Long id = 134L;

    public static Question createQuestion(String code, String title, int index, String hint, boolean mayNotBeApplicable, Long questionnaireId) {
        return new Question(
            id++,
            code,
            title,
            index,
            hint,
            mayNotBeApplicable,
            questionnaireId,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
