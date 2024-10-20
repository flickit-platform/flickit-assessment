package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;

import java.io.InputStream;
import java.util.List;

public interface CreateAttributeScoresFilePort {

    Result generateFile(AttributeValue attributeValue, List<MaturityLevel> maturityLevels);

    record Result(InputStream stream, String text) {
    }
}
