package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;

import java.util.List;

public class SubjectMother {

    private static long id = 134L;

    public static Subject withNoAttributes() {
        return new Subject(id++, "subject" + id, null);
    }

    public static Subject withAttributes(List<Attribute> attributes) {
        return new Subject(id++, "subject" + id, attributes);
    }

    public static Subject withKitId(Long kitId) {
        return new Subject(id++, "subject" + id);
    }
}
