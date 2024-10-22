package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectQuestionnaireJpaRepository extends JpaRepository<SubjectQuestionnaireJpaEntity, Long> {

    List<SubjectQuestionnaireJpaEntity> findAllByKitVersionId(Long kitVersionId);

    @Query("""
            SELECT DISTINCT
                fqr.id As questionnaireId,
                fa.subjectId AS subjectId
            FROM QuestionnaireJpaEntity fqr
            JOIN QuestionJpaEntity fq ON fqr.id = fq.questionnaireId
            JOIN QuestionImpactJpaEntity fqi ON fqi.questionId = fq.id
            JOIN AttributeJpaEntity fa ON fa.id = fqi.attributeId
            WHERE fqr.kitVersionId = :kitVersionId
        """)
    List<SubjectQuestionnaireView> findSubjectQuestionnairePairs(@Param("kitVersionId") long kitVersionId);
}
