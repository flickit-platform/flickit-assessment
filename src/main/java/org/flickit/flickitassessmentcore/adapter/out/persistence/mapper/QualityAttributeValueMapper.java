package org.flickit.flickitassessmentcore.adapter.out.persistence.mapper;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.QualityAttributeValueEntity;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.domain.QualityAttributeValue;

public class QualityAttributeValueMapper {

    private final AssessmentResultMapper assessmentResultMapper = new AssessmentResultMapper();

    public QualityAttributeValue mapToDomainModel(QualityAttributeValueEntity qualityAttributeValueEntity) {
        return new QualityAttributeValue(
            qualityAttributeValueEntity.getId(),
            assessmentResultMapper.mapToDomainModel(qualityAttributeValueEntity.getAssessmentResult()),
            new QualityAttribute(qualityAttributeValueEntity.getQualityAttributeId()),
            new MaturityLevel(qualityAttributeValueEntity.getMaturityLevelId())
        );
    }

    public QualityAttributeValueEntity mapToJpaEntity(QualityAttributeValue qualityAttributeValue) {
        return new QualityAttributeValueEntity(
            qualityAttributeValue.getId(),
            assessmentResultMapper.mapToJpaEntity(qualityAttributeValue.getAssessmentResult()),
            qualityAttributeValue.getQualityAttribute().getId(),
            qualityAttributeValue.getMaturityLevel().getId()
        );
    }


}
