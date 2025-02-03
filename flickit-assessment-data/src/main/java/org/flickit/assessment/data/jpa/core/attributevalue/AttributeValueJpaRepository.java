package org.flickit.assessment.data.jpa.core.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AttributeValueJpaRepository extends JpaRepository<AttributeValueJpaEntity, UUID> {

    List<AttributeValueJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    List<AttributeValueJpaEntity> findByAssessmentResult_assessment_IdAndAttributeIdIn(UUID assessmentId, Collection<Long> attributeIds);

    List<AttributeValueJpaEntity> findAllByIdIn(Collection<UUID> ids);

    AttributeValueJpaEntity findByAttributeIdAndAssessmentResultId(@Param(value = "attributeId") Long attributeId,
                                                                   @Param(value = "assessmentResultId") UUID assessmentResultId);

    @Query("""
            SELECT av
            FROM AttributeValueJpaEntity av
            LEFT JOIN AttributeJpaEntity att ON av.attributeId = att.id
            WHERE att.subjectId = :subjectId
                AND av.assessmentResult.id = :assessmentResultId
                AND att.kitVersionId = av.assessmentResult.kitVersionId
        """)
    List<AttributeValueJpaEntity> findByAssessmentResultIdAndSubjectId(@Param(value = "assessmentResultId") UUID assessmentResultId,
                                                                       @Param(value = "subjectId") Long subjectId);

    @Query("""
            SELECT av as attributeValue,
                att.subjectId as subjectId,
                att as attribute
            FROM AttributeValueJpaEntity av
            LEFT JOIN AttributeJpaEntity att ON av.attributeId = att.id
                AND av.assessmentResult.kitVersionId = att.kitVersionId
                AND av.assessmentResult.id = :assessmentResultId
            WHERE att.subjectId IN :subjectIds
            ORDER BY att.index ASC
        """)
    List<SubjectIdAttributeValueView> findByAssessmentResultIdAndSubjectIdInOrderByIndex(@Param(value = "assessmentResultId") UUID assessmentResultId,
                                                                                         @Param(value = "subjectIds") Collection<Long> subjectIds);

    @Query("""
            SELECT
                av AS attributeValue,
                att.subjectId AS subjectId,
                att AS attribute
            FROM AttributeValueJpaEntity av
            JOIN AttributeJpaEntity att ON av.attributeId = att.id AND av.assessmentResult.kitVersionId = att.kitVersionId
            WHERE av.assessmentResult.id = :assessmentResultId
        """)
    List<SubjectIdAttributeValueView> findAllWithAttributeByAssessmentResultId(@Param(value = "assessmentResultId") UUID assessmentResultId);
}
