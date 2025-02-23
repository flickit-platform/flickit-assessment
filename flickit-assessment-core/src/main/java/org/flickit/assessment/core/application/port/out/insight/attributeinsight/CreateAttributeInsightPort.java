package org.flickit.assessment.core.application.port.out.insight.attributeinsight;

import org.flickit.assessment.core.application.domain.AttributeInsight;

public interface CreateAttributeInsightPort {

    void persist(AttributeInsight attributeInsight);
}
