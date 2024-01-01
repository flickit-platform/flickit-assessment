package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJoinAttributeView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

public class SubjectJoinAttributeViewMother {

    public static SubjectJoinAttributeView subjectJoinAttributeView(SubjectJpaEntity subject, AttributeJpaEntity attribute) {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        var projection = factory.createProjection(SubjectJoinAttributeView.class);
        projection.setSubject(subject);
        projection.setAttribute(attribute);
        return projection;
    }
}
