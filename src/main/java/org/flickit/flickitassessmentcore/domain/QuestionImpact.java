package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QuestionImpact {

    private Long id;
    private Long maturityLevelId;
    private Long qualityAttributeId;
    private Double weight;
}
