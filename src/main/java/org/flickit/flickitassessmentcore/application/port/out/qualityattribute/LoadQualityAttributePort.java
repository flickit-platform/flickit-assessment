package org.flickit.flickitassessmentcore.application.port.out.qualityattribute;

import org.flickit.flickitassessmentcore.domain.QualityAttribute;


public interface LoadQualityAttributePort {

    QualityAttribute loadQualityAttribute(Long qualityAttributeId);

}
