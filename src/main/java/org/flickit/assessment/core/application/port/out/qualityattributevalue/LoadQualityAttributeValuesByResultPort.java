package org.flickit.assessment.core.application.port.out.qualityattributevalue;

import org.flickit.assessment.core.domain.QualityAttributeValue;

import java.util.Set;
import java.util.UUID;

public interface LoadQualityAttributeValuesByResultPort {

    Set<QualityAttributeValue> loadQualityAttributeValuesByResultId(UUID resultId);
}
