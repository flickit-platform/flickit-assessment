package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
            SELECT COUNT(q)
            FROM QuestionnaireJpaEntity q
            WHERE q.kitVersionId = :kitVersionId
        """)
    Long getKitQuestionnaireCount(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            SELECT COUNT(a)
            FROM AttributeJpaEntity a
            WHERE a.kitVersionId = :kitVersionId
        """)
    Long getKitAttributeCount(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            SELECT COUNT(q)
            FROM QuestionJpaEntity q
            WHERE q.kitVersionId = :kitVersionId
        """)
    Long getKitQuestionCount(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            SELECT COUNT(ml)
            FROM MaturityLevelJpaEntity ml
            WHERE ml.kitVersionId = :kitVersionId
        """)
    Long getKitMaturityLevelCount(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            SELECT COUNT(l)
            FROM KitLikeJpaEntity l
            WHERE l.kitId =  :kitId
        """)
    Long getKitLikeCount(@Param(value = "kitId") Long kitId);

    @Query("""
            SELECT COUNT(a)
            FROM AssessmentJpaEntity a
            WHERE a.assessmentKitId = :kitId
        """)
    Long getKitAssessmentCount(@Param(value = "kitId") Long kitId);

    @Query("""
            SELECT s
            FROM SubjectJpaEntity s
            WHERE s.kitVersionId = :kitVersionId
        """)
    List<SubjectJpaEntity> getKitSubjects(@Param(value = "kitVersionId") Long kitVersionId);
}
