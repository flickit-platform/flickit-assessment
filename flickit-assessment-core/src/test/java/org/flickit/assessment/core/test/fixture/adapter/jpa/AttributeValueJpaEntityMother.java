package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;

import java.util.UUID;

public class AttributeValueJpaEntityMother {

    public static AttributeValueJpaEntity attributeValueWithNullMaturityLevel(AssessmentResultJpaEntity assessmentResultJpaEntity, long attributeId) {
        return new AttributeValueJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            attributeId,
            null,
            null
        );
    }
}
