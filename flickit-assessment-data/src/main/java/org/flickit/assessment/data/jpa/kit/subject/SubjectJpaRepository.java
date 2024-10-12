package org.flickit.assessment.data.jpa.kit.subject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, SubjectJpaEntity.EntityId> {

    List<SubjectJpaEntity> findAllByKitVersionIdOrderByIndex(long kitVersionId);

    Optional<SubjectJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    List<SubjectJpaEntity> findAllByIdInAndKitVersionId(Set<Long> ids, long kitVersionId);

    Page<SubjectJpaEntity> findByKitVersionId(long kitVersionId, PageRequest pageRequest);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

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

    @Query("""
            SELECT s.id AS id,
                s.title AS title,
                sq.questionnaireId AS questionnaireId
            FROM SubjectJpaEntity s
            JOIN SubjectQuestionnaireJpaEntity sq ON s.id = sq.subjectId
            WHERE sq.questionnaireId IN :questionnaireIds AND s.kitVersionId = :kitVersionId
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
}
