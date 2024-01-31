package org.flickit.assessment.core.application.port.out.qualityattributevalue;

import org.flickit.assessment.core.application.domain.QualityAttributeValue;

import java.util.List;
import java.util.UUID;

public interface CreateQualityAttributeValuePort {

    List<QualityAttributeValue> persistAll(List<Long> qualityAttributeIds, UUID resultId);
}
