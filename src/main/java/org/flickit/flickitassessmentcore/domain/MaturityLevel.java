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
public class MaturityLevel {

    public MaturityLevel(Long id) {
        this.id = id;
    }

    private Long id;
    private String title;
    private Integer value;
    private AssessmentKit assessmentKit;
    private Set<Assessment> assessments;
    private Set<QualityAttributeValue> qualityAttributeValues;
    private Set<QuestionImpact> questionImpacts;
    private Set<LevelCompetence> levelCompetences;
}
