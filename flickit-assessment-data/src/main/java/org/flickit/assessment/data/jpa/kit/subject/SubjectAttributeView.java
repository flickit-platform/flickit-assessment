package org.flickit.assessment.data.jpa.kit.subject;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

public interface SubjectAttributeView {

    SubjectJpaEntity getSubject();

    AttributeJpaEntity getAttribute();
}
