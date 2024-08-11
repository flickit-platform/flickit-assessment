package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.Attribute;

public interface CreateAttributeAiInsightPort {

    String generateInsight(String fileText, Attribute attribute);
}
