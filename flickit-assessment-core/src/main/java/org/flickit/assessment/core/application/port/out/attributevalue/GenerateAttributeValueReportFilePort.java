package org.flickit.assessment.core.application.port.out.attributevalue;

import java.io.InputStream;
import java.util.UUID;

public interface GenerateAttributeValueReportFilePort {

    InputStream generateReport(UUID assessmentId, Long attributeId);
}
