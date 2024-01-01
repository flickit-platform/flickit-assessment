package org.flickit.assessment.data.jpa.kit.subject;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

public interface SubjectJoinAttributeView {

    SubjectJpaEntity getSubject();

    void setSubject(SubjectJpaEntity subject);

    AttributeJpaEntity getAttribute();

    void setAttribute(AttributeJpaEntity attribute);
}
