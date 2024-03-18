package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.Subject;

import java.util.List;
import java.util.UUID;

public class SubjectMother {

    private static long id = 134L;

    public static Subject withNoAttributes() {
        return new Subject(id++, UUID.randomUUID(), "subject" + id, null);
    }

    public static Subject withAttributes(List<QualityAttribute> attributes) {
        return new Subject(id++, UUID.randomUUID(), "subject" + id, attributes);
    }

}
