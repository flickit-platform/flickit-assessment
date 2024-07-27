package org.flickit.assessment.core.application.port.out.aireport;

import java.io.InputStream;

public interface CreateAssessmentAttributeAiPort {

    String createReport (InputStream inputStream, String attribute);
}
