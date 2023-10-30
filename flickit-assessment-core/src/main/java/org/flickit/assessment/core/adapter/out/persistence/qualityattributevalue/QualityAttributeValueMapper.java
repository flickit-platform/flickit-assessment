package org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
