package org.flickit.assessment.data.jpa.kit.customkit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface KitCustomJpaRepository extends JpaRepository<KitCustomJpaEntity, Long> {

    boolean existsByIdAndKitId(long id, long kitId);

    @Modifying
    @Query("""
            UPDATE KitCustomJpaEntity k
            SET k.title = :title,
                K.code = :code,
                K.customData = :customData,
                k.lastModificationTime = :lastModificationTime,
                k.lastModifiedBy = :lastModifiedBy
            WHERE k.id = :id
        """)
    void update(@Param("id") long id,
                @Param("title") String title,
                @Param("code") String code,
                @Param("customId") String customData,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);
}
