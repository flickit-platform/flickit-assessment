package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.core.application.domain.AttributeValue;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeValuePort {

    AttributeValue load(UUID assessmentResultId, Long attributeId);

    List<AttributeValue> loadAll(UUID assessmentResultId);
}
