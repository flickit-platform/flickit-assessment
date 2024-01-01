package org.flickit.assessment.core.application.port.out.qualityattributevalue;

import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LoadAttributeValueListPort {

    List<QualityAttributeValue> loadAll(UUID assessmentResultId, Map<Long, MaturityLevel> maturityLevels);
}

