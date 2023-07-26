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
public class Question {

    private Long id;
    private String title;
    private List<QuestionImpact> impacts;

    @Override
    public String toString() {
        return title;
    }
}
