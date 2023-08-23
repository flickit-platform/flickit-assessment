package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {

    List<AssessmentJpaEntity> findBySpaceIdOrderByLastModificationTimeDesc(long spaceId, Pageable pageable);

    @Modifying
    @Query("UPDATE AssessmentJpaEntity a SET "+
        "a.title = :title, " +
        "a.colorId = :colorId, " +
        "a.lastModificationTime = :lastModificationTime " +
        "WHERE a.id = :id")
    UUID update(@Param(value = "id") UUID id,
                @Param(value = "title") String title,
                @Param(value = "colorId") Integer colorId,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime);
}
