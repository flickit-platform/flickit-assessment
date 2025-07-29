package org.flickit.assessment.data.jpa.users.usersurvey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserSurveyJpaRepository extends JpaRepository<UserSurveyJpaEntity, Long> {

    Optional<UserSurveyJpaEntity> findByUserId(UUID userId);

    @Modifying
    @Query("""
            UPDATE UserSurveyJpaEntity s
            SET s.dontShowAgain = :dontShowAgain,
                s.lastModificationTime = :lastModificationTime
            WHERE s.userId = :userId
        """
    )
    void updateDontShowAgainByUserId(@Param("userId") UUID userId,
                                     @Param("dontShowAgain") boolean dontShowAgain,
                                     @Param("lastModificationTime") LocalDateTime lastModificationTime);
}
