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
public class EvidenceRelation {
    private UUID id;
    private AssessmentProject assessmentProject;
    private Long metricId;
    private List<Evidence> evidences;

    @Override
    public String toString() {
        return id.toString();
    }
}
