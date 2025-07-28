package org.flickit.assessment.users.application.port.out.usersurvey;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateUserSurveyPort {

    long persist(Param param);

    record Param(UUID userId,
                 UUID assessmentId,
                 boolean dontShowAgain,
                 LocalDateTime currentDateTime) {
    }
}
