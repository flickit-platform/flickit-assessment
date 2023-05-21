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
public class MetricValue {
    private UUID id;
    private AssessmentResult assessmentResult;
    private Long metricId;
    private Long answerId;

    @Override
    public String toString() {
        return id.toString();
    }
}
