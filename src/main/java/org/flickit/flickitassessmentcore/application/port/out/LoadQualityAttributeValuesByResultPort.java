package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

import java.util.Set;
import java.util.UUID;

public interface LoadQualityAttributeValuesByResultPort {

    Set<QualityAttributeValue> loadQualityAttributeValuesByResultId(UUID resultId);
}
