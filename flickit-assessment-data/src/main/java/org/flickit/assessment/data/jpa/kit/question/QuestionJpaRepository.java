package org.flickit.assessment.data.jpa.kit.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    @Query("SELECT q as question, qi as questionImpact " +
        "FROM QuestionJpaEntity q " +
        "LEFT JOIN QuestionImpactJpaEntity qi ON q.id = qi.questionId " +
        "WHERE q.questionnaireId IN (SELECT qu.id FROM QuestionnaireJpaEntity qu " +
        "WHERE qu.assessmentKitId = :assessmentKitId)")
    List<QuestionJoinQuestionImpactView> loadByAssessmentKitId(Long assessmentKitId);

    @Query("SELECT q FROM QuestionJpaEntity q " +
        "WHERE q.questionnaireId IN (SELECT qs.questionnaireId FROM SubjectQuestionnaireJpaEntity qs " +
        "WHERE qs.subjectId = :subjectId)")
    List<QuestionJpaEntity> findBySubjectId(long subjectId);
}
