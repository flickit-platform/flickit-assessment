package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

public class QualityAttributeValueMapper {

    public static QualityAttributeValueJpaEntity mapToJpaEntity(Long qualityAttributeId) {
        return new QualityAttributeValueJpaEntity(
            null,
            null,
            qualityAttributeId,
            null
        );
    }
}
