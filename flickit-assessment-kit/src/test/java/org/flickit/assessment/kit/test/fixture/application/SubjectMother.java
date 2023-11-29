package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.Subject;

import java.time.LocalDateTime;
import java.util.List;

public class SubjectMother {

    public static final Long SUBJECT_ONE_ID = 10L;
    public static final String SUBJECT_ONE_CODE = "Software";
    public static final String SUBJECT_ONE_TITLE = "Software1";
    public static final String SUBJECT_ONE_DESCRIPTION = "description for Software1";
    public static final Long SUBJECT_TWO_ID = 20L;
    public static final String SUBJECT_TWO_CODE = "Team";
    public static final String SUBJECT_TWO_TITLE = "Team1";
    public static final String SUBJECT_TWO_DESCRIPTION = "description for Team1";

    public static final Long SUBJECT_THREE_ID = 30L;
    public static final String SUBJECT_THREE_CODE = "Security";
    public static final String SUBJECT_THREE_TITLE = "Security";
    public static final String SUBJECT_THREE_DESCRIPTION = "description for Security";


    public static List<Subject> oneSubjects() {
        return List.of(subjectOne());
    }

    public static List<Subject> twoSubject() {
        return List.of(subjectOne(), subjectTwo());
    }

    public static List<Subject> threeSubjects() {
        return List.of(subjectOne(), subjectTwo(), subjectThree());
    }

    public static Subject subjectOne() {
        return new Subject(
            SUBJECT_ONE_ID,
            SUBJECT_ONE_CODE,
            SUBJECT_ONE_TITLE,
            1,
            SUBJECT_ONE_DESCRIPTION,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static Subject subjectTwo() {
        return new Subject(
            SUBJECT_TWO_ID,
            SUBJECT_TWO_CODE,
            SUBJECT_TWO_TITLE,
            2,
            SUBJECT_TWO_DESCRIPTION,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static Subject subjectThree() {
        return new Subject(
            SUBJECT_THREE_ID,
            SUBJECT_THREE_CODE,
            SUBJECT_THREE_TITLE,
            3,
            SUBJECT_THREE_DESCRIPTION,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
