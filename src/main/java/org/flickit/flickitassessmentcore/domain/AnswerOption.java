package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AnswerOption {
    private Long id;
    private Question question;
    private String caption;
    private Integer value;
    private Integer index;
    private Set<Answer> answers;
    private Set<AnswerOptionImpact> answerOptionImpacts;
}
