package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

public interface AttributeWithSubjectView {

    AttributeJpaEntity getAttribute();

    SubjectJpaEntity getSubject();
}
