package org.flickit.assessment.data.jpa.core.attributevalue;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.util.UUID;

public interface SubjectRefNumAttributeValueView {

    UUID getSubjectRefNum();

    AttributeValueJpaEntity getAttributeValue();

    AttributeJpaEntity getAttribute();
}
