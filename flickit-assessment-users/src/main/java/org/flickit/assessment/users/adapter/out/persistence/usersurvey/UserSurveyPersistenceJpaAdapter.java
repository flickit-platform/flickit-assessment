package org.flickit.assessment.users.adapter.out.persistence.usersurvey;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.usersurvey.UserSurveyJpaRepository;
import org.flickit.assessment.users.application.domain.UserSurvey;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserSurveyPersistenceJpaAdapter implements LoadUserSurveyPort {

    private final UserSurveyJpaRepository repository;

    @Override
    public Optional<UserSurvey> loadByUserId(UUID userId) {
        return repository.findByUserId(userId)
            .map(UserSurveyMapper::mapToDomain);
    }
}
