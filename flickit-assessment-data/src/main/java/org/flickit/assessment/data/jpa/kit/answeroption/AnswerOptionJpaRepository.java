package org.flickit.assessment.data.jpa.kit.answeroption;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerOptionJpaRepository extends JpaRepository<AnswerOptionJpaEntity, AnswerOptionJpaEntity.EntityId> {

    List<AnswerOptionJpaEntity> findAllByAnswerRangeIdAndKitVersionIdOrderByIndex(long answerRangeId, long kitVersionId);

    Optional<AnswerOptionJpaEntity> findByIdAndKitVersionId(Long id, Long kitVersionId);

    List<AnswerOptionJpaEntity> findAllByIdInAndKitVersionId(List<Long> allAnswerOptionIds, long kitVersionId);

    List<AnswerOptionJpaEntity> findAllByKitVersionId(long kitVersionId);

    List<AnswerOptionJpaEntity> findAllByKitVersionId(long kitVersionId, Sort sort);

    boolean existsByIdAndKitVersionId(Long answerOptionId, Long kitVersionId);

    void deleteByIdAndKitVersionId(Long answerOptionId, Long kitVersionId);

    List<AnswerOptionJpaEntity> findAllByAnswerRangeIdInAndKitVersionId(Collection<Long> answerRangeIds, Long kitVersionId, Sort sort);

    @Modifying
    @Query("""
            UPDATE AnswerOptionJpaEntity a
            SET a.title = :title,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id AND a.kitVersionId = :kitVersionId
        """)
    void updateTitle(@Param("id") Long id,
                     @Param("kitVersionId") Long kitVersionId,
                     @Param("title") String title,
                     @Param("lastModificationTime") LocalDateTime lastModificationTime,
                     @Param("lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE AnswerOptionJpaEntity a
            SET a.index = :index,
                a.title = :title,
                a.value = :value,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id AND a.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") Long id,
                @Param("kitVersionId") long kitVersionId,
                @Param("index") int index,
                @Param("title") String title,
                @Param("value") double value,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);
}
