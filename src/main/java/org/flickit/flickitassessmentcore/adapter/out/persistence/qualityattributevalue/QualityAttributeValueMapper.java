package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

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
