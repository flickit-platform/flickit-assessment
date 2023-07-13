package org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue;

import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

import java.util.List;
import java.util.UUID;

public interface LoadQualityAttributeByResultPort {

    Result loadQualityAttributeByResultId(Param param);

    record Param(UUID resultId) {}

    record Result(List<QualityAttributeValue> qualityAttributeValues) {}
}
