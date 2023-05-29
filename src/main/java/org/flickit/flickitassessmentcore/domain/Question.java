package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Question {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private Integer index;
    private Questionnaire questionnaire;
    private Set<Answer> answer;
    private Set<QuestionImpact> questionImpacts;
    private Set<AnswerOption> answerOptions;
    private Set<Evidence> evidences;
    private Set<QualityAttribute> qualityAttributes;

    @Override
    public String toString() {
        return title;
    }
}
