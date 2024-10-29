package org.flickit.assessment.data.jpa.kit.answerrange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AnswerRangeJpaRepository extends JpaRepository<AnswerRangeJpaEntity, AnswerRangeJpaEntity.EntityId> {

    List<AnswerRangeJpaEntity> findAllByKitVersionId(long kitVersionId);

    @Query("""
        SELECT ar
        FROM AnswerRangeJpaEntity ar
        WHERE ar.kitVersionId = :kitVersionId AND ar.reusable = true
        """)
    Page<AnswerRangeJpaEntity> findReusableByKitVersionId(@Param("kitVersionId") long kitVersionId, Pageable pageable);
}
