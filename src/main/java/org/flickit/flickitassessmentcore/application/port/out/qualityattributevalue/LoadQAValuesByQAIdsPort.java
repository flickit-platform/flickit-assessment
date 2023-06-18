package org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue;

import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

import java.util.List;
import java.util.Set;

public interface LoadQAValuesByQAIdsPort {

    List<QualityAttributeValue> LoadQAValuesByQAIds(Set<Long> qaIds);
}
