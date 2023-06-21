package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LevelCompetence {
    private Long id;
    private MaturityLevel maturityLevel;
    private Integer value;
    private MaturityLevel maturityLevelCompetence;
}
