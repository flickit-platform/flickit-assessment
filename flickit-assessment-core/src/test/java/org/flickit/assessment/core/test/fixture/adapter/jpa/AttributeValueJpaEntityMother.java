package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;

import java.util.UUID;

public class AttributeValueJpaEntityMother {

    public static QualityAttributeValueJpaEntity attributeValueWithNullMaturityLevel(AssessmentResultJpaEntity assessmentResultJpaEntity, UUID attributeRefNum) {
        return new QualityAttributeValueJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            attributeRefNum,
            null,
            null
        );
    }
}
