package org.flickit.assessment.data.jpa.kit.assessmentkit;

import jakarta.annotation.Nullable;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface AssessmentKitJpaRepository extends
    JpaRepository<AssessmentKitJpaEntity, Long>,
    JpaSpecificationExecutor<AssessmentKitJpaEntity> {

    @Query("""
            SELECT k
            FROM AssessmentKitJpaEntity k
            JOIN KitVersionJpaEntity kv ON k.id = kv.kit.id
            WHERE kv.id = :kitVersionId
        """)
    Optional<AssessmentKitJpaEntity> findByKitVersionId(long kitVersionId);

    @Query("""
            SELECT k
            FROM AssessmentKitJpaEntity k
            JOIN KitVersionJpaEntity kv ON k.id = kv.kit.id
            WHERE kv.id IN :kitVersionIds
        """)
    List<AssessmentKitJpaEntity> findByKitVersionIds(Set<Long> kitVersionIds);

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
            WHERE k.published = TRUE
                AND k.isPrivate = FALSE
                AND EXISTS (SELECT 1 FROM KitLanguageJpaEntity kl
                    WHERE kl.kitId = k.id
                        AND (:languageIds IS NULL OR kl.langId IN :languageIds))
        """)
    Page<KitWithExpertGroupView> findAllPublishedAndNotPrivate(@Nullable
                                                               @Param("languageIds")
                                                               Collection<Integer> languageIds,
                                                               Pageable pageable);

    @Query("""
            SELECT k AS kit, g AS expertGroup,
            EXISTS (
                SELECT 1 FROM KitUserAccessJpaEntity kua
                WHERE kua.kitId = k.id AND kua.userId = :userId
            ) AS kitUserAccess
            FROM AssessmentKitJpaEntity k
            LEFT JOIN ExpertGroupJpaEntity g ON k.expertGroupId = g.id
            WHERE k.published = TRUE
                AND k.isPrivate = FALSE
                AND EXISTS (SELECT 1 FROM KitLanguageJpaEntity kl
                    WHERE kl.kitId = k.id
                        AND (:languageIds IS NULL OR kl.langId IN :languageIds))
        """)
    Page<KitWithExpertGroupView> findAllPublishedAndNotPrivateByUserId(@Param("userId") UUID userId,
                                                                       @Nullable
                                                                       @Param("languageIds")
                                                                       Collection<Integer> languageIds,
                                                                       Pageable pageable);

    @Query("""
            SELECT k AS kit, g AS expertGroup,
            EXISTS (
                SELECT 1 FROM KitUserAccessJpaEntity kua
                WHERE kua.kitId = k.id AND kua.userId = :userId
            ) AS kitUserAccess
            FROM AssessmentKitJpaEntity k
            LEFT JOIN ExpertGroupJpaEntity g ON k.expertGroupId = g.id
            WHERE k.published = TRUE
                AND EXISTS (SELECT 1 FROM KitLanguageJpaEntity kl
                    WHERE kl.kitId = k.id
                        AND (:languageIds IS NULL OR kl.langId IN :languageIds))
                AND EXISTS (SELECT 1 FROM KitUserAccessJpaEntity kua
                    WHERE kua.kitId = k.id
                        AND k.isPrivate = TRUE
                        AND kua.userId = :userId)
        """)
    Page<KitWithExpertGroupView> findAllPublishedAndPrivateByUserId(@Param("userId")
                                                                    UUID userId,
                                                                    @Nullable
                                                                    @Param("languageIds")
                                                                    Collection<Integer> languageIds,
                                                                    Pageable pageable);

    @Query("""
            SELECT k AS kit, g AS expertGroup,
            EXISTS (
                SELECT 1 FROM KitUserAccessJpaEntity kua
                WHERE kua.kitId = k.id AND kua.userId = :userId
            ) AS kitUserAccess
            FROM AssessmentKitJpaEntity k
            LEFT JOIN ExpertGroupJpaEntity g ON k.expertGroupId = g.id
            WHERE k.published = TRUE
                AND (k.isPrivate = FALSE
                    OR EXISTS (SELECT 1 FROM KitUserAccessJpaEntity kua
                        WHERE kua.kitId = k.id
                            AND k.isPrivate = TRUE
                            AND kua.userId = :userId))
                AND EXISTS (SELECT 1 FROM KitLanguageJpaEntity kl
                    WHERE kl.kitId = k.id
                        AND (:languageIds IS NULL OR kl.langId IN :languageIds))
        """)
    Page<KitWithExpertGroupView> findAllPublished(@Param("userId")
                                                  UUID userId,
                                                  @Nullable
                                                  @Param("languageIds")
                                                  Collection<Integer> languageIds,
                                                  Pageable pageable);

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
            SELECT
                k AS kit,
                kv.id AS draftVersionId
            FROM AssessmentKitJpaEntity k
            LEFT JOIN KitVersionJpaEntity kv ON kv.kit.id = k.id AND kv.status = :updatingStatusId
            LEFT JOIN KitUserAccessJpaEntity kua ON k.id = kua.kitId AND kua.userId = :userId
            WHERE k.expertGroupId = :expertGroupId
                AND (:includeUnpublished = TRUE OR k.published = TRUE)
                AND (k.isPrivate = FALSE OR kua.userId IS NOT NULL)
            ORDER BY k.published DESC, k.lastModificationTime DESC
        """)
    Page<KitWithDraftVersionIdView> findExpertGroupKitsOrderByPublishedAndModificationTimeDesc(
        @Param("expertGroupId") long expertGroupId,
        @Param("userId") UUID userId,
        @Param("includeUnpublished") boolean includeUnpublishedKits,
        @Param("updatingStatusId") int updatingStatusId,
        Pageable pageable);

    @Query("""
            SELECT k.kitVersionId
            FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId
        """)
    Optional<Long> loadKitVersionId(@Param("kitId") long kitId);

    @Query("""
            SELECT k.id
            FROM AssessmentKitJpaEntity k
            WHERE k.id = :kitId
              AND k.published
              AND ((k.isPrivate = false AND k.price = 0)
                  OR EXISTS (
                    SELECT 1
                    FROM KitUserAccessJpaEntity kua
                    WHERE kua.userId = :userId AND kua.kitId = k.id
                )
              )
        """)
    Optional<Long> existsByUserId(@Param("kitId") long kitId, @Param("userId") UUID userId);
}
