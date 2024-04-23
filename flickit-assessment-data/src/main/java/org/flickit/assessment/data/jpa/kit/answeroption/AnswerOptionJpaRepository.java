package org.flickit.assessment.data.jpa.kit.answeroption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnswerOptionJpaRepository extends JpaRepository<AnswerOptionJpaEntity, Long> {

    @Modifying
    @Query("""
        UPDATE AnswerOptionJpaEntity a
        SET a.title = :title,
        a.lastModificationTime = :lastModificationTime,
        a.lastModifiedBy = :lastModifiedBy
        WHERE a.id = :id
        """)
    void update(@Param("id") Long id,
                @Param("title") String title,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    List<AnswerOptionJpaEntity> findByQuestionId(Long questionId);
}
