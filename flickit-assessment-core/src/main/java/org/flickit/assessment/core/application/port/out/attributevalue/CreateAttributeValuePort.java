package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.core.application.domain.AttributeValue;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CreateAttributeValuePort {

    List<AttributeValue> persistAll(Set<Long> attributeIds, UUID resultId);
}
