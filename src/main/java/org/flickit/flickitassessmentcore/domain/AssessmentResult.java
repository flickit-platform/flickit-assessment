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
public class AssessmentResult {
    private UUID id;
    private Assessment assessment;
    private Long maturityLevelId;
    private Boolean isValid;

    @Override
    public String toString() {
        return id.toString();
    }
}
