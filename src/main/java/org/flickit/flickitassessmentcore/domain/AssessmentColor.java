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
public class AssessmentColor {
    private Long id;
    private String title;
    private String colorCode;
    private Set<Assessment> assessments;

    @Override
    public String toString() {
        return title;
    }
}
