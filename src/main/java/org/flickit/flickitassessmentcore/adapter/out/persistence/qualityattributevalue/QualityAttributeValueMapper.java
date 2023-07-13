package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

public class QualityAttributeValueMapper {

    public static QualityAttributeValueJpaEntity mapToJpaEntity(Long qualityAttributeId) {
        return new QualityAttributeValueJpaEntity(
            null,
            null,
            qualityAttributeId,
            null
        );
    }

    public static QualityAttributeValue mapToDomainModel(QualityAttributeValueJpaEntity qualityAttributeValueEntity) {
        return new QualityAttributeValue(
            qualityAttributeValueEntity.getId(),
            new QualityAttribute(qualityAttributeValueEntity.getQualityAttributeId()),
            new MaturityLevel(qualityAttributeValueEntity.getMaturityLevelId()),
            qualityAttributeValueEntity.getAssessmentResult().getId()
        );
    }

    public static QualityAttributeValueJpaEntity mapToJpaEntity(QualityAttributeValue qualityAttributeValue) {
        return new QualityAttributeValueJpaEntity(
            qualityAttributeValue.getId(),
            new AssessmentResultJpaEntity(qualityAttributeValue.getResultId()),
            qualityAttributeValue.getQualityAttribute().getId(),
            qualityAttributeValue.getMaturityLevel().getId()
        );
    }


}
