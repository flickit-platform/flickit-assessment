package org.flickit.assessment.data.jpa.kit.kitcustom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface KitCustomJpaRepository extends JpaRepository<KitCustomJpaEntity, Long> {

    boolean existsByIdAndKitId(long id, long kitId);

    Optional<KitCustomJpaEntity> findByIdAndKitId(long id, long kitId);

    @Modifying
    @Query("""
            UPDATE KitCustomJpaEntity k
            SET k.title = :title,
                k.code = :code,
                k.customData = :customData,
                k.lastModificationTime = :lastModificationTime,
                k.lastModifiedBy = :lastModifiedBy
            WHERE k.id = :id
        """)
    void update(@Param("id") long id,
                @Param("title") String title,
                @Param("code") String code,
                @Param("customData") String customData,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT k.lastModificationTime
            FROM KitCustomJpaEntity k
            WHERE k.id = :kitCustomId
        """)
    LocalDateTime loadLastModificationTime(@Param("kitCustomId") Long kitCustomId);
}
