package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.Attribute;

import java.io.InputStream;

public interface CreateAssessmentAttributeAiPort {

    String createReport (InputStream inputStream, Attribute attribute);
}
