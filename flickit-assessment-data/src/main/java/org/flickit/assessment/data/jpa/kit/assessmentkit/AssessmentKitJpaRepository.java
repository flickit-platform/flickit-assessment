package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AssessmentKitJpaRepository extends JpaRepository<AssessmentKitJpaEntity, Long> {

    @Query("SELECT u FROM UserJpaEntity u " +
        "WHERE u.id IN (SELECT ku.id.userId FROM KitUserAccessJpaEntity ku WHERE ku.id.kitId = :kitId)")
    Page<UserJpaEntity> findAllKitUsers(Long kitId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE AssessmentKitJpaEntity a SET
                a.lastMajorModificationTime = :lastMajorModificationTime
            WHERE a.id = :kitId
        """)
    void updateLastMajorModificationTime(@Param("kitId") Long kitId,
                                         @Param("lastMajorModificationTime") LocalDateTime lastMajorModificationTime);

    @Query("""
        SELECT k.lastMajorModificationTime FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId
        """)
    LocalDateTime loadLastMajorModificationTime(@Param("kitId") Long kitId);

    @Modifying
    @Query("""
            UPDATE AssessmentKitJpaEntity a SET a.kitVersionId = :kitVersionId
            WHERE a.id = :id
        """)
    void updateKitVersionId(@Param(value = "id") Long id, @Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            SELECT
                (SELECT COUNT(DISTINCT id) FROM QuestionnaireJpaEntity WHERE kitVersionId = k.kitVersionId) AS questionnaireCount,
                (SELECT COUNT(DISTINCT id) FROM AttributeJpaEntity WHERE kitVersionId = k.kitVersionId) AS attributeCount,
                (SELECT COUNT(DISTINCT id) FROM QuestionJpaEntity WHERE kitVersionId = k.kitVersionId) AS questionCount,
                (SELECT COUNT(DISTINCT id) FROM MaturityLevelJpaEntity WHERE kitVersionId = k.kitVersionId) AS maturityLevelCount,
                (SELECT COUNT(DISTINCT userId) FROM KitLikeJpaEntity WHERE kitId = k.id) AS likeCount,
                (SELECT COUNT(DISTINCT id) FROM AssessmentJpaEntity WHERE assessmentKitId = k.id) AS assessmentCount
            FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId
        """)
    CountKitStatsView countKitStats(@Param(value = "kitId") long kitId);
}
