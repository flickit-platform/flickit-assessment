package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Subject;

public class SubjectMother {

    private static long id = 134L;

    public static Subject withNoAttributes() {
        return new Subject(id++);
    }
}
