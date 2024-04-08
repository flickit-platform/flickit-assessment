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
                COUNT(DISTINCT questionnaire.id) AS questionnaireCount,
                COUNT(DISTINCT att.id) AS attributeCount,
                COUNT(DISTINCT q.id) AS questionCount,
                COUNT(DISTINCT ml.id) AS maturityLevelCount,
                COUNT(DISTINCT l.userId) AS likeCount,
                COUNT(DISTINCT a.id) AS assessmentCount
            FROM AssessmentKitJpaEntity k
            LEFT JOIN QuestionnaireJpaEntity questionnaire ON k.kitVersionId = questionnaire.kitVersionId
            LEFT JOIN AttributeJpaEntity att ON k.kitVersionId = att.kitVersionId
            LEFT JOIN QuestionJpaEntity q ON k.kitVersionId = q.kitVersionId
            LEFT JOIN MaturityLevelJpaEntity ml ON k.kitVersionId = ml.kitVersionId
            LEFT JOIN KitLikeJpaEntity l ON k.id = l.kitId
            LEFT JOIN AssessmentJpaEntity a ON k.id = a.assessmentKitId
            WHERE k.id = :kitId
        """)
    CountKitStatsView countKitStats(@Param(value = "kitId") long kitId);
}
