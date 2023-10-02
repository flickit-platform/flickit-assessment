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
        "a.deletionTime=:deletionTime AND " +
        "(a.assessmentKitId=:kitId OR :kitId IS NULL) AND " +
        "r.lastModificationTime = (SELECT MAX(ar.lastModificationTime) FROM AssessmentResultJpaEntity ar WHERE ar.assessment.id = a.id) " +
        "ORDER BY a.lastModificationTime DESC")
    Page<AssessmentListItemView> findBySpaceIdOrderByLastModificationTimeDesc(List<Long> spaceIds, Long kitId, long deletionTime, Pageable pageable);

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
}
