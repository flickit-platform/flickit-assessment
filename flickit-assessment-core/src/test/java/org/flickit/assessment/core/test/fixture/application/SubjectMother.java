package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;

import java.util.List;

public class SubjectMother {

    private static long id = 134L;

    public static Subject subjectWithWeight(int weight) {
        return new Subject(id++, "subject" + id, weight, null);
    }

    public static Subject subjectWithAttributes(List<Attribute> attributes) {
        return new Subject(id++, "subject" + id, 1, attributes);
    }
}
