package org.flickit.assessment.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QualityAttributeValue {
    private UUID id;
    private QualityAttribute qualityAttribute;
    private MaturityLevel maturityLevel;

    @Override
    public String toString() {
        return id.toString();
    }
}
