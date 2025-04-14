package org.flickit.assessment.data.jpa.kit.subject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, SubjectJpaEntity.EntityId> {

    List<SubjectJpaEntity> findAllByKitVersionIdOrderByIndex(long kitVersionId);

    Optional<SubjectJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    List<SubjectJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> ids, long kitVersionId);

    Page<SubjectJpaEntity> findByKitVersionId(long kitVersionId, Pageable pageable);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

    List<SubjectJpaEntity> findAllByKitVersionId(long kitVersionId);

    int countByKitVersionId(long kitVersionId);

    @Modifying
    @Query("""
            UPDATE SubjectJpaEntity s
            SET s.title = :title,
                s.index = :index,
                s.description = :description,
                s.lastModificationTime = :lastModificationTime,
                s.lastModifiedBy = :lastModifiedBy
            WHERE s.id = :id AND s.kitVersionId = :kitVersionId
        """)
    void update(@Param(value = "id") long id,
                @Param(value = "kitVersionId") long kitVersionId,
                @Param(value = "title") String title,
                @Param(value = "index") int index,
                @Param(value = "description") String description,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy
    );

    @Modifying
    @Query("""
            UPDATE SubjectJpaEntity s
            SET s.code = :code,
                s.title = :title,
                s.index = :index,
                s.description = :description,
                s.weight = :weight,
                s.translations = :translations,
                s.lastModificationTime = :lastModificationTime,
                s.lastModifiedBy = :lastModifiedBy
            WHERE s.id = :id AND s.kitVersionId = :kitVersionId
        """)
    void update(@Param(value = "id") long id,
                @Param(value = "kitVersionId") long kitVersionId,
                @Param(value = "code") String code,
                @Param(value = "title") String title,
                @Param(value = "index") int index,
                @Param(value = "description") String description,
                @Param(value = "weight") int weight,
                @Param(value = "translations") String translations,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT
                s.id AS id,
                s.title AS title,
                sq.questionnaireId AS questionnaireId
            FROM SubjectQuestionnaireJpaEntity sq
            JOIN SubjectJpaEntity s ON s.id = sq.subjectId AND s.kitVersionId = :kitVersionId
            WHERE sq.questionnaireId IN :questionnaireIds AND sq.kitVersionId = :kitVersionId
        """)
    List<SubjectWithQuestionnaireIdView> findAllWithQuestionnaireIdByKitVersionId(@Param(value = "questionnaireIds") List<Long> questionnaireIds,
                                                                                  @Param(value = "kitVersionId") long kitVersionId);

    @Query("""
            SELECT s
            FROM SubjectJpaEntity s
            JOIN SubjectQuestionnaireJpaEntity sq ON s.id = sq.subjectId
            WHERE sq.questionnaireId = :questionnaireId AND s.kitVersionId = :kitVersionId
            ORDER BY s.index
        """)
    List<SubjectJpaEntity> findAllByQuestionnaireIdAndKitVersionId(@Param("questionnaireId") long questionnaireId,
                                                                   @Param("kitVersionId") long kitVersionId);

    @Query("""
            SELECT s
            FROM SubjectJpaEntity s
            LEFT JOIN AttributeJpaEntity a ON a.subjectId = s.id AND a.kitVersionId = s.kitVersionId
            WHERE s.kitVersionId = :kitVersionId AND a.id IS NULL
        """)
    List<SubjectJpaEntity> findAllByKitVersionIdAndWithoutAttributes(@Param("kitVersionId") long kitVersionId);

    @Query("""
        SELECT s as subject,
               a as attribute
        FROM SubjectJpaEntity s
        JOIN AttributeJpaEntity a ON s.id = a.subjectId AND s.kitVersionId = a.kitVersionId
        WHERE s.kitVersionId = :kitVersionId
        """)
    List<SubjectJoinAttributeView> findWithAttributesByKitVersionId(Long kitVersionId);
}
