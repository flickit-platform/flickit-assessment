package org.flickit.assessment.data.jpa.users.usersurvey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSurveyJpaRepository extends JpaRepository<UserSurveyJpaEntity, Long> {

    Optional<UserSurveyJpaEntity> findByUserId(UUID userId);
}
