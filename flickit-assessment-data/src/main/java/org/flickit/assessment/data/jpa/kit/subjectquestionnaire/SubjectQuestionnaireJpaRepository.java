package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectQuestionnaireJpaRepository extends JpaRepository<SubjectQuestionnaireJpaEntity, Long> {

    @Query("""
            FROM SubjectQuestionnaireJpaEntity sq
            where sq.subjectId in
                (SELECT s.id FROM SubjectJpaEntity s WHERE s.kitVersionId = :kitVersionId)
        """)
    List<SubjectQuestionnaireJpaEntity> findAllByKitVersionId(@Param(value = "kitVersionId") Long kitVersionId);
}
