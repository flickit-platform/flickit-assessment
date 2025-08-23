package org.flickit.assessment.core.application.port.out.insight.attribute;

import org.flickit.assessment.core.application.domain.insight.AttributeInsight;

import java.util.Optional;
import java.util.UUID;

public interface LoadAttributeInsightPort {

    Optional<AttributeInsight> load(UUID assessmentResultId, Long attributeId);
}
