package org.flickit.flickitassessmentcore.test.fixture.application;

import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.Subject;

import java.util.List;

public class SubjectMother {

    private static long id = 134L;

    public static Subject withNoAttributes() {
        return new Subject(id++);
    }

    public static Subject withAttributes(List<QualityAttribute> attributes) {
        return new Subject(id++, attributes);
    }
}
