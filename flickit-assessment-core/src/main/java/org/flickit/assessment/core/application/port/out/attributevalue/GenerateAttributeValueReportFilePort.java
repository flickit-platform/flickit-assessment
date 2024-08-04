package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.io.InputStream;
import java.util.List;

public interface GenerateAttributeValueReportFilePort {

    InputStream generateReport(AttributeValue attributeValue, List<MaturityLevel> maturityLevels);
}
