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
public class AssessmentColor {
    private Long id;
    private String title;
    private String color_code;
    private List<AssessmentProject> assessmentProjects;

    @Override
    public String toString() {
        return title;
    }
}
