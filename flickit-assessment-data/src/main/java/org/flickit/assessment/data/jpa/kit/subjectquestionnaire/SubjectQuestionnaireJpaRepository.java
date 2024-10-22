package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectQuestionnaireJpaRepository extends JpaRepository<SubjectQuestionnaireJpaEntity, Long> {

    List<SubjectQuestionnaireJpaEntity> findAllByKitVersionId(Long kitVersionId);

    @Query("""
            SELECT DISTINCT
                qr.id As questionnaireId,
                a.subjectId AS subjectId
            FROM QuestionnaireJpaEntity qr
            JOIN QuestionJpaEntity q ON qr.id = q.questionnaireId
            JOIN QuestionImpactJpaEntity qi ON qi.questionId = q.id
            JOIN AttributeJpaEntity a ON a.id = qi.attributeId
            WHERE qr.kitVersionId = :kitVersionId
        """)
    List<SubjectQuestionnaireView> findSubjectQuestionnairePairs(@Param("kitVersionId") long kitVersionId);
}
