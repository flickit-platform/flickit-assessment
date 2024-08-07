package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.io.InputStream;
import java.util.List;

public interface CreateAttributeScoresFilePort {

    InputStream generateFile(AttributeValue attributeValue, List<MaturityLevel> maturityLevels);
}
