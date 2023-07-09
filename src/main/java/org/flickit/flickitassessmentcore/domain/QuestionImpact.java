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

    private Integer level;

    private Long maturityLevelId;

    private Question question;

    private Long qualityAttributeId;

    private Integer weight;
}
