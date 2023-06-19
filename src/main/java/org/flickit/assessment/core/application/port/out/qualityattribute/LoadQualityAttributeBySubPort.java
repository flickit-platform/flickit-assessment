package org.flickit.assessment.core.application.port.out.qualityattribute;

import org.flickit.assessment.core.domain.QualityAttribute;

import java.util.List;

public interface LoadQualityAttributeBySubPort {

    List<QualityAttribute> loadQABySubId(Long subId);
}
