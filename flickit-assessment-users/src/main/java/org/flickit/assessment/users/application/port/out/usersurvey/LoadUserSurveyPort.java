package org.flickit.assessment.users.application.port.out.usersurvey;

import org.flickit.assessment.users.application.domain.UserSurvey;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserSurveyPort {

    Optional<UserSurvey> loadByUserId(UUID userId);

    Optional<Long> loadIdByUserId(UUID userId);

    boolean existsByUserId(UUID userid);
}
