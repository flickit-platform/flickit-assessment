package org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue;

import java.util.List;
import java.util.UUID;

public interface CreateQualityAttributeValuePort {

    void persistAllWithAssessmentResultId(List<Param> params, UUID resultId);

    record Param(Long qualityAttributeId) {
    }
}
