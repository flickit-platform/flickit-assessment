package org.flickit.assessment.data.jpa.kit.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, Long> {

    List<SubjectJpaEntity> findAllByAssessmentKitId(Long assessmentKitId);

    @Modifying
    @Query("UPDATE SubjectJpaEntity s SET " +
        "s.title = :title, " +
        "s.index = :index, " +
        "s.description = :description, " +
        "s.lastModificationTime = :lastModificationTime " +
        "WHERE s.id = :id")
    void update(
        @Param(value = "id") long id,
        @Param(value = "title") String title,
        @Param(value = "index") int index,
        @Param(value = "description") String description,
        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime
    );

    @Query("SELECT s as subject, a as attribute " +
            "FROM SubjectJpaEntity s " +
            "LEFT JOIN AttributeJpaEntity a ON s.id = a.subjectId " +
            "WHERE s.assessmentKitId = :kitId")
    List<SubjectJoinAttributeView> loadByAssessmentKitId(Long kitId);
}
