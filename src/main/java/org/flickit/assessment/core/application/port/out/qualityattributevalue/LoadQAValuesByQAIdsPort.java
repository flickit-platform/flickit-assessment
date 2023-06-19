package org.flickit.assessment.core.application.port.out.qualityattributevalue;

import org.flickit.assessment.core.domain.QualityAttributeValue;

import java.util.List;
import java.util.Set;

public interface LoadQAValuesByQAIdsPort {

    List<QualityAttributeValue> loadQAValuesByQAIds(Set<Long> qaIds);
}
