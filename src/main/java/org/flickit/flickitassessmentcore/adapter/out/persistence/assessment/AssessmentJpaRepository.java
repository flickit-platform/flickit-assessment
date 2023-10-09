package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {

    @Query("SELECT a as assessment, r.maturityLevelId as maturityLevelId, r.isValid as isCalculateValid " +
        "FROM AssessmentJpaEntity a " +
        "LEFT JOIN AssessmentResultJpaEntity r " +
        "ON a.id = r.assessment.id " +
        "WHERE a.spaceId IN :spaceIds AND " +
        "a.deleted=:deleted AND " +
        "(a.assessmentKitId=:kitId OR :kitId IS NULL) AND " +
        "r.lastModificationTime = (SELECT MAX(ar.lastModificationTime) FROM AssessmentResultJpaEntity ar WHERE ar.assessment.id = a.id) " +
        "ORDER BY a.lastModificationTime DESC")
    Page<AssessmentListItemView> findBySpaceIdOrderByLastModificationTimeDesc(List<Long> spaceIds, Long kitId, boolean deleted, Pageable pageable);

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
        "a.deletionTime = :deletionTime, " +
        "a.deleted = true " +
        "WHERE a.id = :id")
    void setDeletedAndDeletionTimeById(@Param(value = "id") UUID id, @Param(value = "deletionTime") Long deletionTime);

    boolean existsByIdAndDeleted(@Param(value = "id") UUID id, @Param(value = "deleted") boolean deleted);

    @Query("SELECT COUNT(a) " +
        "FROM AssessmentJpaEntity a " +
        "WHERE a.assessmentKitId = :assessmentKitId")
    int countTotalByKitId(Long assessmentKitId);

    @Query("SELECT COUNT(a) " +
        "FROM AssessmentJpaEntity a " +
        "WHERE a.assessmentKitId = :assessmentKitId AND a.deleted = true")
    int countDeletedByKitId(Long assessmentKitId);

    @Query("SELECT COUNT(a) " +
        "FROM AssessmentJpaEntity a " +
        "WHERE a.assessmentKitId = :assessmentKitId AND a.deleted = false")
    int countNotDeletedByKitId(Long assessmentKitId);
}
