package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MaturityLevel {

    private Long id;
    private String title;
    private Integer value;
    private List<LevelCompetence> levelCompetences;

    public MaturityLevel(Long id) {
        this.id = id;
    }
}
