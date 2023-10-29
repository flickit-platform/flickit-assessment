package org.flickit.flickitassessmentcore.test.fixture.adapter.jpa;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;

import java.util.UUID;

public class AttributeValueJpaEntityMother {

    public static QualityAttributeValueJpaEntity attributeValueWithNullMaturityLevel(AssessmentResultJpaEntity assessmentResultJpaEntity, long attributeId) {
        return new QualityAttributeValueJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            attributeId,
            null
        );
    }
}
