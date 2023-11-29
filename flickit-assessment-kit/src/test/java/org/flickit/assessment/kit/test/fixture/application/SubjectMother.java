package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Subject;

import java.time.LocalDateTime;

public class SubjectMother {

    private static Long id = 134L;
    private static int index = 1;

    public static Subject subjectWithTitle(String title) {
        return new Subject(
            id++,
            "c-" + title,
            title,
            index++,
            "Description",
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

}
