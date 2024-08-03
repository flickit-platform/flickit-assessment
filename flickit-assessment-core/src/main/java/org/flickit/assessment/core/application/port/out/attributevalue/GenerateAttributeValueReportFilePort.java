package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.core.application.domain.AttributeValue;

import java.io.InputStream;
import java.util.UUID;

public interface GenerateAttributeValueReportFilePort {

    InputStream generateReport(UUID assessmentId, AttributeValue attributeValue);
}
