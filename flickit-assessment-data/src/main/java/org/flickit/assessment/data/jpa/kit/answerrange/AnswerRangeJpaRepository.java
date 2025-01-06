package org.flickit.assessment.data.jpa.kit.answerrange;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerRangeJpaRepository extends JpaRepository<AnswerRangeJpaEntity, AnswerRangeJpaEntity.EntityId> {

    List<AnswerRangeJpaEntity> findAllByKitVersionId(long kitVersionId);

    Page<AnswerRangeJpaEntity> findByKitVersionIdAndReusableTrue(long kitVersionId, Pageable pageable);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    Optional<AnswerRangeJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE AnswerRangeJpaEntity a
            SET a.title = :title,
                a.code = :code,
                a.reusable = :reusable,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :answerRangeId and a.kitVersionId = :kitVersionId
            """)
    void update(@Param("answerRangeId") long answerRangeId,
                @Param("kitVersionId") long kitVersionId,
                @Param("title") String title,
                @Param("code") String code,
                @Param("reusable") boolean reusable,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT a as answerRange, o as answerOption
            FROM AnswerRangeJpaEntity a
            JOIN AnswerOptionJpaEntity o on o.answerRangeId = a.id AND a.kitVersionId = o.kitVersionId
            WHERE a.kitVersionId = :kitVersionId AND a.reusable = TRUE
        """)
    List<AnswerRangeJoinOptionView> findAllReusableWithOptionsByKitVersionId(@Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT a as answerRange, o as answerOption
            FROM AnswerRangeJpaEntity a
            LEFT JOIN AnswerOptionJpaEntity o on o.answerRangeId = a.id AND a.kitVersionId = o.kitVersionId
            WHERE a.kitVersionId = :kitVersionId
        """)
    List<AnswerRangeJoinOptionView> findAllWithOptionsByKitVersionId(@Param("kitVersionId") long kitVersionId);
}
