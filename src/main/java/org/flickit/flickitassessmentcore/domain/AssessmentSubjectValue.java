package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentSubjectValue {
    private UUID id;
    private AssessmentSubject assessmentSubject;
    private MaturityLevel maturityLevel;

    @Override
    public String toString() {
        return id.toString();
    }
}
