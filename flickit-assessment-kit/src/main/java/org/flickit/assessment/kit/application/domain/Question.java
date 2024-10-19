package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Question {

    private final long id;
    private final String code;
    private final String title;
    private final int index;
    private final String hint;
    private final Boolean mayNotBeApplicable;
    private final Boolean advisable;
    @Setter
    private List<QuestionImpact> impacts;
    @Setter
    private List<AnswerOption> options;
    private final Long questionnaireId;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;

    public static String generateCode(int index) {
        return "m" + index;
    }
}
