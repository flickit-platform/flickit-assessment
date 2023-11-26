package org.flickit.assessment.data.jpa.kit.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, Long> {

    List<SubjectJpaEntity> findAllByAssessmentKit_Id(Long assessmentKitId);

    @Modifying
    @Query("UPDATE SubjectJpaEntity s SET " +
        "s.title = :title, " +
        "s.description = :description, " +
        "s.index = :index, " +
        "s.lastModificationTime = :lastModificationTime " +
        "WHERE s.code = :code AND s.assessmentKit.id = :kitId")
    void updateByCodeAndAssessmentKitId(
        @Param(value = "kitId") long kitId,
        @Param(value = "code") String code,
        @Param(value = "title") String title,
        @Param(value = "description") String description,
        @Param(value = "index") int index,
        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime
    );
}
