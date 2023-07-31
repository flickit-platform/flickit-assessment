package org.flickit.flickitassessmentcore.domain;

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
    private UUID assessmentResultId;
    private Long questionnaireId;
    private Long questionId;
    private Long answerOptionId;
    private Boolean isNotApplicable;

    @Override
    public String toString() {
        return id.toString();
    }
}
