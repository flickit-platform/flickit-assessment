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
public class AssessmentSubject {
    private Long id;
    private String code;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime lastModificationDate;
    private Integer index;
    private AssessmentKit assessmentKit;
    private Set<Questionnaire> questionnaires;

    public AssessmentSubject(Long assessmentSubjectId) {
        this.id = assessmentSubjectId;
    }

    @Override
    public String toString() {
        return title;
    }
}
