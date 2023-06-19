package org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue;

import org.flickit.assessment.core.domain.MaturityLevel;
import org.flickit.assessment.core.domain.QualityAttribute;
import org.flickit.assessment.core.domain.QualityAttributeValue;

public class QualityAttributeValueMapper {

    public static QualityAttributeValue mapToDomainModel(QualityAttributeValueJpaEntity qualityAttributeValueEntity) {
        return new QualityAttributeValue(
            qualityAttributeValueEntity.getId(),
            new QualityAttribute(qualityAttributeValueEntity.getQualityAttributeId()),
            new MaturityLevel(qualityAttributeValueEntity.getMaturityLevelId())
        );
    }

    public static QualityAttributeValueJpaEntity mapToJpaEntity(QualityAttributeValue qualityAttributeValue) {
        return new QualityAttributeValueJpaEntity(
            qualityAttributeValue.getId(),
            null, // TODO
            qualityAttributeValue.getQualityAttribute().getId(),
            qualityAttributeValue.getMaturityLevel().getId()
        );
    }


}
