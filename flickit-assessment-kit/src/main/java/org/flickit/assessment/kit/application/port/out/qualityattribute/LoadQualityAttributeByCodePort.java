package org.flickit.assessment.kit.application.port.out.qualityattribute;

import org.flickit.assessment.kit.application.domain.Attribute;

public interface LoadQualityAttributeByCodePort {

    Attribute loadByCode(String code, Long kitId);
}
