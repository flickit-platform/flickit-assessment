package org.flickit.assessment.core.application.port.out.insight.attributeinsight;

import org.flickit.assessment.core.application.domain.AttributeInsight;

import java.util.Collection;

public interface CreateAttributeInsightPort {

    void persist(AttributeInsight attributeInsight);

    void persistAll(Collection<AttributeInsight> attributeInsights);
}
