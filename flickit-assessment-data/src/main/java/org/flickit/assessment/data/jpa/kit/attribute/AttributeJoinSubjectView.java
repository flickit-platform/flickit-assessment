package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

public interface AttributeJoinSubjectView {

    AttributeJpaEntity getAttribute();

    SubjectJpaEntity getSubject();
}
