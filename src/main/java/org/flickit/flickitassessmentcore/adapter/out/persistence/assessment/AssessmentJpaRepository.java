package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {

    @Query("SELECT a as assessment, r.maturityLevelId as maturityLevelId, r.isValid as isCalculateValid " +
        "FROM AssessmentJpaEntity a " +
        "LEFT JOIN AssessmentResultJpaEntity r " +
        "ON a.id = r.assessment.id " +
        "WHERE a.spaceId=:spaceId AND " +
        "a.deletionTime=:deletionTime AND " +
        "r.lastModificationTime = (SELECT MAX(ar.lastModificationTime) FROM AssessmentResultJpaEntity ar WHERE ar.assessment.id = a.id) " +
        "ORDER BY a.lastModificationTime DESC")
    Page<AssessmentListItemView> findBySpaceIdOrderByLastModificationTimeDesc(long spaceId, long deletionTime, Pageable pageable);

    @Modifying
    @Query("UPDATE AssessmentJpaEntity a SET " +
        "a.title = :title, " +
        "a.colorId = :colorId, " +
        "a.code = :code, " +
        "a.lastModificationTime = :lastModificationTime " +
        "WHERE a.id = :id")
    void update(@Param(value = "id") UUID id,
                @Param(value = "title") String title,
                @Param(value = "code") String code,
                @Param(value = "colorId") Integer colorId,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime);

    @Modifying
    @Query("UPDATE AssessmentJpaEntity a SET " +
        "a.deletionTime = :deletionTime " +
        "WHERE a.id = :id")
    void setDeletionTimeById(@Param(value = "id") UUID id, @Param(value = "deletionTime") Long deletionTime);

    Optional<AssessmentJpaEntity> findByIdAndDeletionTime(@Param(value = "id") UUID id, @Param(value = "deletionTime") Long deletionTime);

    @Query("FROM AssessmentJpaEntity a " +
        "LEFT JOIN EvidenceJpaEntity e " +
        "ON a.id = e.assessmentId " +
        "WHERE e.id=:evidenceId AND " +
        "a.deletionTime = :deletionTime")
    Optional<AssessmentJpaEntity> findByEvidenceIdAndDeletionTime(@Param(value = "evidenceId") UUID evidenceId, @Param(value = "deletionTime") Long deletionTime);
}
