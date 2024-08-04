package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.core.application.domain.AttributeValue;

import java.util.UUID;

public interface LoadAttributeValuePort {

    AttributeValue load(UUID assessmentResultId, Long attributeId);
}
