package org.flickit.assessment.core.application.port.out.insight.attribute;

import org.flickit.assessment.core.application.domain.insight.AttributeInsight;

import java.util.Collection;

public interface CreateAttributeInsightPort {

    void persist(AttributeInsight attributeInsight);

    void persistAll(Collection<AttributeInsight> attributeInsights);
}
