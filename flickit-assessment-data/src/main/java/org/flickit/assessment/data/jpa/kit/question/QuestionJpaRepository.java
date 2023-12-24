package org.flickit.assessment.data.jpa.kit.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, Long> {

    @Query("SELECT q FROM QuestionJpaEntity q " +
        "WHERE q.questionnaireId IN (SELECT i.id FROM QuestionnaireJpaEntity i WHERE i.assessmentKitId = :kitId)")
    List<QuestionJpaEntity> findByKitId(@Param("kitId") Long kitId);

    @Modifying
    @Query("UPDATE QuestionJpaEntity q SET " +
        "q.title = :title, " +
        "q.description = :description, " +
        "q.index = :index, " +
        "q.mayNotBeApplicable = :mayNotBeApplicable," +
        "q.lastModificationTime = :lastModificationTime " +
        "WHERE q.id = :id")
    void update(@Param("id") Long id,
                @Param("title") String title,
                @Param("index") Integer index,
                @Param("description") String description,
                @Param("mayNotBeApplicable") Boolean mayNotBeApplicable,
                @Param("lastModificationTime") LocalDateTime lastModificationTime);

    @Query("""
        SELECT
          qr.title as questionnaireTitle,
          qsn.id as questionId,
          qsn.index as questionIndex,
          qsn.title as questionTitle,
          ans as answer,
          qi as questionImpact,
          ov as optionImpact,
          ao.index as optionIndex,
          ao.title as optionTitle
        FROM QuestionJpaEntity qsn
        LEFT JOIN AnswerJpaEntity ans on ans.questionId = qsn.id and ans.assessmentResult.id = :assessmentResultId
        LEFT JOIN AnswerOptionJpaEntity ao on ans.answerOptionId = ao.id
        LEFT JOIN QuestionnaireJpaEntity qr on qsn.questionnaireId = qr.id
        LEFT JOIN QuestionImpactJpaEntity qi on qsn.id = qi.questionId
        LEFT JOIN AnswerOptionImpactJpaEntity ov on ov.questionImpact.id = qi.id and ov.optionId = ans.answerOptionId
        WHERE
          qi.qualityAttributeId = :attributeId
          AND qi.maturityLevel.id = :maturityLevelId
        ORDER BY qr.title asc, qsn.index asc
    """)
    List<QuestionJoinAnswerView> findImpactFullQuestionsScore(@Param("attributeId") Long attributeId,
                                                              @Param("maturityLevelId") Long maturityLevelId,
                                                              @Param("assessmentResultId") UUID assessmentResultId);
}
