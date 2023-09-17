package org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue;

import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LoadAttributeValueListPort {

    List<QualityAttributeValue> loadAttributeValues(UUID assessmentResultId, Map<Long, MaturityLevel> maturityLevels);
}

