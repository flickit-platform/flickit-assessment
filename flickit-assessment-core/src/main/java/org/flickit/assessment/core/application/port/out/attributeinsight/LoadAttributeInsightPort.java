package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.AttributeInsight;

import java.util.Optional;
import java.util.UUID;

public interface LoadAttributeInsightPort {

    Optional<AttributeInsight> load(UUID assessmentResultId, Long attributeId);
}
