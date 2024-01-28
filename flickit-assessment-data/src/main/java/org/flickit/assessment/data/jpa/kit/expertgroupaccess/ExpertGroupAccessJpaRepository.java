package org.flickit.assessment.data.jpa.kit.expertgroupaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpertGroupAccessJpaRepository extends JpaRepository<ExpertGroupAccessJpaEntity, Long> {

    @Query("""
            SELECT COALESCE(COUNT(e.id), 0) > 0 as isMember
            FROM ExpertGroupAccessJpaEntity e
            WHERE e.expertGroupId = :expertGroupId AND e.userId = :userId
        """)
    boolean checkUserIsMember(@Param(value = "expertGroupId") long expertGroupId,
                              @Param(value = "userId") UUID userId);
}
