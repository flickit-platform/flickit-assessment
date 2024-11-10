package org.flickit.assessment.data.jpa.core.subjectvalue;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

public interface SubjectValueWithSubjectView {

    SubjectJpaEntity getSubject();

    SubjectValueJpaEntity getSubjectValue();
}
