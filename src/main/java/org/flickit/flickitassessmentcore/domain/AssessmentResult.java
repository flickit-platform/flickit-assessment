package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentResult {
    private UUID id;
    private AssessmentProject assessmentProject;
    private List<Answer> answers;
    private List<QualityAttributeValue> qualityAttributeValues;

    @Override
    public String toString() {
        return id.toString();
    }
}
