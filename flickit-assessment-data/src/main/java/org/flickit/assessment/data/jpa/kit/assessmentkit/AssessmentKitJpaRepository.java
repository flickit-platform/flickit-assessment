package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
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
            FROM KitVersionJpaEntity kv
            JOIN QuestionnaireJpaEntity questionnaire
                ON kv.id = questionnaire.kitVersionId
            JOIN AttributeJpaEntity att
                ON kv.id = att.kitVersionId
            JOIN QuestionJpaEntity q
                ON kv.id = q.kitVersionId
            JOIN MaturityLevelJpaEntity ml
                ON kv.id = ml.kitVersionId
            JOIN KitLikeJpaEntity l
                ON kv.kit.id = l.kitId
            JOIN AssessmentJpaEntity a
                ON kv.kit.id = a.assessmentKitId
            WHERE kv.id = :kitVersionId
        """)
    KitStatsView getKitStats(@Param(value = "kitVersionId") Long kitVersionId);
}
