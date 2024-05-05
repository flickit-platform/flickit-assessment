package org.flickit.assessment.data.jpa.users.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Modifying
    @Query("""
        UPDATE SpaceJpaEntity s SET
            s.lastModificationTime = :lastModificationTime,
            s.lastModifiedBy = :lastModifiedBy,
            s.title = :title
        WHERE
            s.id = :id
        """)
    void update(long id, String title,
                LocalDateTime lastModificationTime, UUID lastModifiedBy);
}
