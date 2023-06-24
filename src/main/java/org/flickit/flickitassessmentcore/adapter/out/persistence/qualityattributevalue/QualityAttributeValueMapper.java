package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;

public class QualityAttributeValueMapper {

    public static QualityAttributeValueJpaEntity mapToJpaEntity(CreateQualityAttributeValuePort.Param param) {
        return new QualityAttributeValueJpaEntity(
            null,
            null,
            param.qualityAttributeId(),
            null
        );
    }
}
