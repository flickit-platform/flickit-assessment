package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultMapper;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

public class QualityAttributeValueMapper {

    public static QualityAttributeValue mapToDomainModel(QualityAttributeValueJpaEntity qualityAttributeValueEntity) {
        return new QualityAttributeValue(
            qualityAttributeValueEntity.getId(),
            AssessmentResultMapper.mapToDomainModel(qualityAttributeValueEntity.getAssessmentResult()),
            new QualityAttribute(qualityAttributeValueEntity.getQualityAttributeId()),
            new MaturityLevel(qualityAttributeValueEntity.getMaturityLevelId())
        );
    }

    public static QualityAttributeValueJpaEntity mapToJpaEntity(QualityAttributeValue qualityAttributeValue) {
        return new QualityAttributeValueJpaEntity(
            qualityAttributeValue.getId(),
            AssessmentResultMapper.mapToJpaEntity(qualityAttributeValue.getAssessmentResult()),
            qualityAttributeValue.getQualityAttribute().getId(),
            qualityAttributeValue.getMaturityLevel().getId()
        );
    }


}
