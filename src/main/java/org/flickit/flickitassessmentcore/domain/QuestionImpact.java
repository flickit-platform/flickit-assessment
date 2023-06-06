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
public class QuestionImpact {
    private Long id;
    private Integer level;
    private MaturityLevel maturityLevel;
    private Question question;
    private QualityAttribute qualityAttribute;
    private Integer weight;
}
