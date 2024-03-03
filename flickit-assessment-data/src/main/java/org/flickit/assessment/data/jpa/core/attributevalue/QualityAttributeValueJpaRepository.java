package org.flickit.assessment.data.jpa.core.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QualityAttributeValueJpaRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

    @Query("""
        SELECT qav FROM QualityAttributeValueJpaEntity qav
        LEFT JOIN AttributeJpaEntity a ON a.id = :qualityAttributeId AND qav.attributeRefNum = a.refNum
        WHERE qav.assessmentResult.id = :assessmentResultId
        """)
    QualityAttributeValueJpaEntity findByQualityAttributeIdAndAssessmentResult_Id(Long qualityAttributeId, UUID assessmentResultId);

    List<QualityAttributeValueJpaEntity> findByAssessmentResult_assessment_IdAndAttributeRefNumIn(UUID assessmentId, List<UUID> attributeRefNums);

    @Query("""
        SELECT av
        FROM QualityAttributeValueJpaEntity av
        LEFT JOIN AttributeJpaEntity att ON av.attributeRefNum = att.refNum and av.kitVersionId = att.kitVersionId and av.assessmentResult.id = :resultId
        WHERE att.subject.id = :subjectId
        """)
    List<QualityAttributeValueJpaEntity> findByAssessmentResultIdAndSubjectId(UUID resultId, Long subjectId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("update QualityAttributeValueJpaEntity a set a.confidenceValue = :confidenceValue where a.id = :id")
    void updateConfidenceValueById(@Param(value = "id") UUID id,
                                   @Param(value = "confidenceValue") Double confidenceValue);

    List<QualityAttributeValueJpaEntity> findByAssessmentResultIdAndKitVersionId(UUID assessmentResultId, Long kitVersionId);
}
