package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.AttributeInsight;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeInsightsPort {

    List<AttributeInsight> loadInsights(UUID assessmentResultId);
}
