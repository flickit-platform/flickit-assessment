package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Subject;

import java.util.List;

public class SubjectMother {

    private static long id = 134L;

    public static Subject subjectWithWeightAndAttributes(int weight, List<Attribute> attributes) {
        return new Subject(++id,
            "subject",
            "description" + id,
            weight,
            attributes);
    }
}
