package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AssessmentKitJpaRepository extends JpaRepository<AssessmentKitJpaEntity, Long> {

    @Query("SELECT k.expertGroupId FROM AssessmentKitJpaEntity k where k.id = :id")
    Optional<Long> loadKitExpertGroupId(@Param("id") Long id);

    @Query("SELECT u FROM UserJpaEntity u " +
        "WHERE u.id IN (SELECT ku.id.userId FROM KitUserAccessJpaEntity ku WHERE ku.id.kitId = :kitId)")
    Page<UserJpaEntity> findAllKitUsers(Long kitId, Pageable pageable);

    @Modifying
    @Query("""
        UPDATE AssessmentKitJpaEntity a SET
                a.lastEffectiveModificationTime = :lastEffectiveModificationTime,
            WHERE a.id = :kitId
        """)
    void updateById(Long kitId, LocalDateTime lastEffectiveModificationTime);

    @Query("""
        SELECT k.lastEffectiveModificationTime FROM AssessmentKitJpaEntity k " +
        "WHERE k.id = :kitId)
        """)
    LocalDateTime loadLastEffectiveModificationTime(Long kitId);
}
