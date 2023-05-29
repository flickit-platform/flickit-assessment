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
public class QualityAttribute {
    private Long id;
    private String code;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private AssessmentSubject assessmentSubject;
    private Integer index;
    private Integer weight;
    private Set<QualityAttributeValue> qualityAttributeValues;
    private Set<QuestionImpact> questionImpacts;
    private Set<Question> questions;

    @Override
    public String toString() {
        return title;
    }
}
