package org.flickit.assessment.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Answer {
    private UUID id;
    private AssessmentResult assessmentResult;
    private Question question;
    private AnswerOption answerOption;

    @Override
    public String toString() {
        return id.toString();
    }
}
