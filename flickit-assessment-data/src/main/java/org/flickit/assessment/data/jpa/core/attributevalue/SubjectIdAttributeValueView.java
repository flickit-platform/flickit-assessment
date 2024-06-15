package org.flickit.assessment.data.jpa.core.attributevalue;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

public interface SubjectIdAttributeValueView {

    Long getSubjectId();

    AttributeValueJpaEntity getAttributeValue();

    AttributeJpaEntity getAttribute();
}
