package org.flickit.assessment.data.jpa.core.attribute;

import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

public interface AttributeMaturityLevelSubjectView {

    AttributeJpaEntity getAttribute();

    AttributeValueJpaEntity getAttributeValue();

    MaturityLevelJpaEntity getMaturityLevel();

    SubjectJpaEntity getSubject();
}
