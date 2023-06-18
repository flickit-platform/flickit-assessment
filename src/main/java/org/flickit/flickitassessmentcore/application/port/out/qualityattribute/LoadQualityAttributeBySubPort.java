package org.flickit.flickitassessmentcore.application.port.out.qualityattribute;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;

import java.util.List;

public interface LoadQualityAttributeBySubPort {

    List<QualityAttribute> loadQABySubId(Long subId);
}
