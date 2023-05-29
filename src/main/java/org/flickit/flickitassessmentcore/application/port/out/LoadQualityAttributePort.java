package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;


public interface LoadQualityAttributePort {

    QualityAttribute loadQualityAttribute(Long qualityAttributeId);

}
