package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.Attribute;

import java.io.InputStream;

public interface CreateAttributeAiInsightPort {

    String generateInsight(InputStream inputStream, Attribute attribute);
}
