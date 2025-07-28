package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.UserSurvey;

public class UserSurveyMother {

    private static long id = 152L;

    public static UserSurvey createWithCompletedAndDontShowAgain(boolean completed, boolean dontShowAgain) {

        return new UserSurvey(id++,
            completed,
            dontShowAgain
        );
    }

    public static UserSurvey createSimpleUserSurvey() {

        return new UserSurvey(id++,
            false,
            false
        );
    }
}
