package org.flickit.assessment.data.jpa.kit.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, Long> {

    List<SubjectJpaEntity> findAllByKitVersionIdOrderByIndex(Long kitVersionId);

    @Modifying
    @Query("""
        UPDATE SubjectJpaEntity s SET
            s.title = :title,
            s.index = :index,
            s.description = :description,
            s.lastModificationTime = :lastModificationTime,
            s.lastModifiedBy = :lastModifiedBy
            WHERE s.id = :id
        """)
    void update(
        @Param(value = "id") long id,
        @Param(value = "title") String title,
        @Param(value = "index") int index,
        @Param(value = "description") String description,
        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
        @Param(value = "lastModifiedBy") UUID lastModifiedBy
    );

    @Query("""
            SELECT s as subject
            FROM SubjectJpaEntity s
            JOIN FETCH s.attributes a
            WHERE s.kitVersionId = :kitVersionId
        """)
    List<SubjectJpaEntity> loadByKitVersionIdWithAttributes(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            SELECT s as subject
            FROM SubjectJpaEntity s
            WHERE s.id = :id AND s.kitVersionId = :kitVersionId
        """)
    Optional<SubjectJpaEntity> findByIdAndKitVersionId(@Param(value = "id") long id, @Param(value = "kitVersionId") long kitVersionId);

    @Query("""
            SELECT s as subject
            FROM SubjectJpaEntity s
            LEFT JOIN KitVersionJpaEntity kv On kv.id = s.kitVersionId
            WHERE s.id = :id AND kv.kit.id = :kitId
        """)
    Optional<SubjectJpaEntity> findByIdAndKitId(@Param("kitId") long kitId, @Param("id") long id);

    @Query("""
            SELECT s.refNum
            FROM SubjectJpaEntity s
            WHERE s.id = :subjectId
        """)
    UUID findRefNumById(@Param(value = "subjectId") Long subjectId);

    @Query("""
            SELECT s.id AS id, s.title AS title, sq.questionnaireId AS questionnaireId
            FROM SubjectJpaEntity s
            JOIN SubjectQuestionnaireJpaEntity sq
                ON s.id = sq.subjectId
            WHERE sq.questionnaireId IN :questionnaireIds
        """)
    List<SubjectWithQuestionnaireIdView> findAllWithQuestionnaireIdByKitVersionId(@Param(value = "questionnaireIds") List<Long> questionnaireIds);

    @Query("""
            SELECT s
            FROM SubjectJpaEntity s
            JOIN SubjectQuestionnaireJpaEntity sq ON s.id = sq.subjectId
            WHERE sq.questionnaireId = :questionnaireId
            ORDER BY s.index
    """)
    List<SubjectJpaEntity> findAllByQuestionnaireId(long questionnaireId);
}
