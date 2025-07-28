package org.flickit.assessment.users.application.port.out.usersurvey;

import java.util.UUID;

public interface UpdateUserSurveyPort {

    void updateDontShowAgain(UUID userId, boolean dontShowAgain);
}
