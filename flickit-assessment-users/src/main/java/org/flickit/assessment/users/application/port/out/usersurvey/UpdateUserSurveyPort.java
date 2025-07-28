package org.flickit.assessment.users.application.port.out.usersurvey;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateUserSurveyPort {

    void updateDontShowAgain(Param param);

    record Param(UUID userId, boolean dontShowAgain, LocalDateTime lastModificationTime) {
    }
}
