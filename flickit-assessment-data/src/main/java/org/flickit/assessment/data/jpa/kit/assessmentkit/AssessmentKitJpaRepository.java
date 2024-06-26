package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentKitJpaRepository extends JpaRepository<AssessmentKitJpaEntity, Long> {

    List<AssessmentKitJpaEntity> findAllByKitVersionIdIn(List<Long> kitVersionIds);

    @Query("""
            SELECT u
            FROM UserJpaEntity u
            WHERE u.id IN (SELECT ku.userId FROM KitUserAccessJpaEntity ku WHERE ku.kitId = :kitId)
        """)
    Page<UserJpaEntity> findAllKitUsers(Long kitId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE AssessmentKitJpaEntity a
            SET a.lastMajorModificationTime = :lastMajorModificationTime
            WHERE a.id = :kitId
        """)
    void updateLastMajorModificationTime(@Param("kitId") Long kitId,
                                         @Param("lastMajorModificationTime") LocalDateTime lastMajorModificationTime);

    @Query("""
            SELECT k.lastMajorModificationTime
            FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId
        """)
    LocalDateTime loadLastMajorModificationTime(@Param("kitId") Long kitId);

    @Modifying
    @Query("""
            UPDATE AssessmentKitJpaEntity a
            SET a.kitVersionId = :kitVersionId
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

    @Query("""
            SELECT k AS kit, g AS expertGroup
            FROM AssessmentKitJpaEntity k
            LEFT JOIN ExpertGroupJpaEntity g ON k.expertGroupId = g.id
            WHERE k.published = TRUE AND k.isPrivate = FALSE
            ORDER BY k.title
        """)
    Page<KitWithExpertGroupView> findAllPublishedAndNotPrivateOrderByTitle(Pageable pageable);

    @Query("""
            SELECT k AS kit, g AS expertGroup
            FROM AssessmentKitJpaEntity k
            LEFT JOIN ExpertGroupJpaEntity g ON k.expertGroupId = g.id
            JOIN KitUserAccessJpaEntity kua ON k.id = kua.kitId
            WHERE k.published = TRUE AND k.isPrivate = TRUE
                AND kua.userId = :userId
            ORDER BY k.title
        """)
    Page<KitWithExpertGroupView> findAllPublishedAndPrivateByUserIdOrderByTitle(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT
                k.id AS id,
                COUNT(DISTINCT l.userId) AS likeCount,
                COUNT(DISTINCT a.id) AS assessmentCount
            FROM AssessmentKitJpaEntity k
            LEFT JOIN KitLikeJpaEntity l ON k.id = l.kitId
            LEFT JOIN AssessmentJpaEntity a ON k.id = a.assessmentKitId
            WHERE k.id IN :kitIds
            GROUP BY k.id
        """)
    List<CountKitStatsView> countKitStats(@Param(value = "kitIds") List<Long> kitIds);

    @Query("""
            SELECT COUNT(a)
            FROM AssessmentKitJpaEntity k
            JOIN AssessmentJpaEntity a ON k.id = a.assessmentKitId
            WHERE k.id = :kitId
        """)
    long countAllKitAssessments(@Param("kitId") Long kitId);

    @Query("""
            SELECT k
            FROM AssessmentKitJpaEntity k
            WHERE k.expertGroupId = :expertGroupId
                AND (:includeUnpublished = TRUE OR k.published = TRUE)
                AND (k.isPrivate = FALSE
                    OR (k.isPrivate AND k.id IN (SELECT kua.kitId FROM KitUserAccessJpaEntity kua WHERE kua.userId = :userId)))
            ORDER BY k.published desc, k.lastModificationTime desc
        """)
    Page<AssessmentKitJpaEntity> findExpertGroupKitsOrderByPublishedAndModificationTimeDesc(
        @Param("expertGroupId") long expertGroupId,
        @Param("userId") UUID userId,
        @Param("includeUnpublished") boolean includeUnpublishedKits,
        PageRequest pageable);

    @Query("""
            SELECT k.kitVersionId
            FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId
        """)
    Optional<Long> loadKitVersionId(@Param("kitId") long kitId);

    @Query("""
            SELECT k.id
            FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId and k.published AND (k.isPrivate = FALSE
                OR (k.isPrivate = TRUE
                AND (k.id IN (SELECT kua.kitId FROM KitUserAccessJpaEntity kua WHERE kua.userId  = :userId))))
        """)
    Optional<Long> existsByUserId(@Param("kitId") long kitId, @Param("userId") UUID userId);

    @Query("""
            SELECT k
            FROM AssessmentKitJpaEntity k
            WHERE LOWER(k.title) LIKE LOWER(CONCAT('%', :queryTerm, '%')) AND k.published = TRUE
                AND (k.isPrivate = FALSE OR (k.isPrivate
                    AND k.id IN (SELECT kua.kitId FROM KitUserAccessJpaEntity kua WHERE kua.userId = :userId)))
        """)
    Page<AssessmentKitJpaEntity> findAllByTitleAndUserId(@Param("queryTerm") String query,
                                                         @Param("userId") UUID userId,
                                                         Pageable pageable);
}
