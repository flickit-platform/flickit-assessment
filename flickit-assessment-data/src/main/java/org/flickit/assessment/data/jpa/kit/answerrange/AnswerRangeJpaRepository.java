package org.flickit.assessment.data.jpa.kit.answerrange;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRangeJpaRepository extends JpaRepository<AnswerRangeJpaEntity, AnswerRangeJpaEntity.EntityId> {

    List<AnswerRangeJpaEntity> findAllByKitVersionId(long kitVersionId);
}
