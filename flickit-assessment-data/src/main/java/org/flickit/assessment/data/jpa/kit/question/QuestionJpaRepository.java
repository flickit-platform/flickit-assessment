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
        "WHERE q.questionnaireId IN (SELECT i.id FROM QuestionnaireJpaEntity i WHERE i.kitId = :kitId)")
    List<QuestionJpaEntity> findByKitId(@Param("kitId") Long kitId);

    @Modifying
    @Query("""
        UPDATE QuestionJpaEntity q SET
        q.title = :title,
        q.hint = :hint,
        q.index = :index,
        q.mayNotBeApplicable = :mayNotBeApplicable,
        q.lastModificationTime = :lastModificationTime,
        q.lastModifiedBy = :lastModifiedBy
        WHERE q.id = :id
        """)
    void update(@Param("id") Long id,
                @Param("title") String title,
                @Param("index") Integer index,
                @Param("hint") String hint,
                @Param("mayNotBeApplicable") Boolean mayNotBeApplicable,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

}
