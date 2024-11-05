package org.flickit.assessment.data.jpa.kit.answerrange;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRangeJpaRepository extends JpaRepository<AnswerRangeJpaEntity, AnswerRangeJpaEntity.EntityId> {

    List<AnswerRangeJpaEntity> findAllByKitVersionId(long kitVersionId);

    Page<AnswerRangeJpaEntity> findByKitVersionIdAndReusableTrue(long kitVersionId, Pageable pageable);
}
