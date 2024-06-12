package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LoadAttributeValueListPort {

    List<AttributeValue> loadAll(UUID assessmentResultId, Map<Long, MaturityLevel> maturityLevels);
}

