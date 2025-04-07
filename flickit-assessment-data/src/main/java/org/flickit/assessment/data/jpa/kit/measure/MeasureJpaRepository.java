package org.flickit.assessment.data.jpa.kit.measure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeasureJpaRepository extends JpaRepository<MeasureJpaEntity, MeasureJpaEntity.EntityId> {

    List<MeasureJpaEntity> findAllByKitVersionId(long kitVersionId);

    List<MeasureJpaEntity> findAllByKitVersionIdOrderByIndex(Long activeVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    Optional<MeasureJpaEntity> findByCodeAndKitVersionId(String code, Long kitVersionId);

    void deleteByIdAndKitVersionId(long measureId, long kitVersionId);

    List<MeasureJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> ids, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE MeasureJpaEntity m
            SET m.title = :title,
                m.code = :code,
                m.index = :index,
                m.description = :description,
                m.lastModificationTime = :lastModificationTime,
                m.lastModifiedBy = :lastModifiedBy
            WHERE m.id = :id AND m.kitVersionId = :kitVersionId
        """)
    void update(@Param(value = "id") long id,
                @Param(value = "kitVersionId") long kitVersionId,
                @Param(value = "title") String title,
                @Param(value = "code") String code,
                @Param(value = "index") int index,
                @Param(value = "description") String description,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy
    );

    @Query("""
            SELECT
                m as measure,
                COUNT(DISTINCT question.id) as questionCount
            FROM MeasureJpaEntity m
            LEFT JOIN QuestionJpaEntity question ON m.id = question.measureId AND m.kitVersionId = question.kitVersionId
            WHERE m.kitVersionId = :kitVersionId
            GROUP BY m.id, m.kitVersionId, m.index
            ORDER BY m.index
        """)
    Page<MeasureListItemView> findAllWithQuestionCountByKitVersionId(long kitVersionId, Pageable pageable);
}
