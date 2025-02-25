package org.flickit.assessment.core.application.port.out.insight.attribute;

import org.flickit.assessment.core.application.domain.insight.AttributeInsight;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeInsightsPort {

    List<AttributeInsight> loadInsights(UUID assessmentResultId);
}
