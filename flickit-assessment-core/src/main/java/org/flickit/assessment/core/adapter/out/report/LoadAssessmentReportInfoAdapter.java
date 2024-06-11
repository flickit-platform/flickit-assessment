package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.minio.MinioAdapter;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadAssessmentReportInfoAdapter implements LoadAssessmentReportInfoPort {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final AssessmentJpaRepository assessmentRepository;
    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final AssessmentKitJpaRepository assessmentKitRepository;
    private final ExpertGroupJpaRepository expertGroupRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AttributeJpaRepository attributeRepository;
    private final QualityAttributeValueJpaRepository qualityAttributeValueRepository;
    private final MinioAdapter minioAdapter;

    @Override
    public Result load(UUID assessmentId) {
        if (!assessmentRepository.existsByIdAndDeletedFalse(assessmentId))
            throw new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND);

        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        AssessmentJpaEntity assessment = assessmentResultEntity.getAssessment();
        AssessmentKitJpaEntity assessmentKitEntity = assessmentKitRepository.findById(assessment.getAssessmentKitId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND));

        ExpertGroupJpaEntity expertGroupEntity = expertGroupRepository.findById(assessmentKitEntity.getExpertGroupId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_EXPERT_GROUP_NOT_FOUND));

        long kitVersionId = assessmentResultEntity.getKitVersionId();
        var maturityLevelEntities = maturityLevelRepository.findAllByKitVersionIdOrderByIndex(kitVersionId);

        Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntity = maturityLevelEntities.stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        AssessmentReportItem assessmentReportItem = new AssessmentReportItem(assessmentId,
            assessment.getTitle(),
            buildAssessmentKitItem(expertGroupEntity, assessmentKitEntity, maturityLevelEntities),
            MaturityLevelMapper.mapToDomainModel(idToMaturityLevelEntity.get(assessmentResultEntity.getMaturityLevelId()), null),
            assessmentResultEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid(),
            AssessmentColor.valueOfById(assessment.getColorId()),
            assessment.getCreationTime(),
            assessment.getLastModificationTime());

        List<AssessmentSubjectReportItem> subjects = buildSubjectReportItems(assessmentResultEntity, idToMaturityLevelEntity);

        return new Result(assessmentReportItem, subjects);
    }

    private AssessmentReportItem.AssessmentKitItem buildAssessmentKitItem(ExpertGroupJpaEntity expertGroupEntity,
                                                                          AssessmentKitJpaEntity assessmentKitEntity,
                                                                          List<MaturityLevelJpaEntity> maturityLevelJpaEntities) {
        AssessmentReportItem.AssessmentKitItem.ExpertGroup expertGroup =
            new AssessmentReportItem.AssessmentKitItem.ExpertGroup(expertGroupEntity.getId(),
                expertGroupEntity.getTitle(),
                minioAdapter.createDownloadLink(expertGroupEntity.getPicture(), EXPIRY_DURATION));

        return new AssessmentReportItem.AssessmentKitItem(assessmentKitEntity.getId(),
            assessmentKitEntity.getTitle(),
            assessmentKitEntity.getSummary(),
            maturityLevelJpaEntities.size(),
            expertGroup);
    }

    private List<AssessmentSubjectReportItem> buildSubjectReportItems(AssessmentResultJpaEntity assessmentResult,
                                                                      Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntity) {
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResult.getId());

        Set<Long> subjectIds = subjectValueEntities.stream()
            .map(SubjectValueJpaEntity::getSubjectId)
            .collect(Collectors.toSet());

        Map<Long, SubjectValueJpaEntity> subjectIdToSubjectValueEntity = subjectValueEntities.stream()
            .collect(toMap(SubjectValueJpaEntity::getSubjectId, Function.identity()));

        Map<Long, List<AttributeJpaEntity>> subjectIdToAttributeEntities =
            attributeRepository.findAllBySubjectIdInAndKitVersionId(subjectIds, assessmentResult.getKitVersionId()).stream()
                .collect(groupingBy(AttributeJpaEntity::getSubjectId));

        List<SubjectJpaEntity> subjectEntities = subjectRepository.findAllByIdInAndKitVersionId(subjectIds, assessmentResult.getKitVersionId());

        return subjectEntities.stream()
            .map(e -> {
                Long maturityLevelId = subjectIdToSubjectValueEntity.get(e.getId()).getMaturityLevelId();
                MaturityLevelJpaEntity maturityLevelEntity = idToMaturityLevelEntity.get(maturityLevelId);
                MaturityLevel subjectMaturityLevel = MaturityLevelMapper.mapToDomainModel(maturityLevelEntity, null);
                return new AssessmentSubjectReportItem(e.getId(),
                    e.getTitle(),
                    e.getIndex(),
                    e.getDescription(),
                    subjectIdToSubjectValueEntity.get(e.getId()).getConfidenceValue(),
                    subjectMaturityLevel,
                    subjectIdToAttributeEntities.get(e.getId()).stream()
                        .map(x -> buildAttributeReportItem(assessmentResult.getId(), idToMaturityLevelEntity, x))
                        .toList());
            }).toList();
    }

    private AttributeReportItem buildAttributeReportItem(UUID assessmentResultId,
                                                         Map<Long, MaturityLevelJpaEntity> idToMaturityLevelEntities,
                                                         AttributeJpaEntity attributeEntity) {
        QualityAttributeValueJpaEntity attributeValueEntity =
            qualityAttributeValueRepository.findByAttributeIdAndAssessmentResult_Id(attributeEntity.getId(), assessmentResultId);

        var maturityLevelEntity = idToMaturityLevelEntities.get(attributeValueEntity.getMaturityLevelId());
        var maturityLevel = MaturityLevelMapper.mapToDomainModel(maturityLevelEntity, null);
        return new AttributeReportItem(attributeEntity.getId(),
            attributeEntity.getTitle(),
            attributeEntity.getDescription(),
            attributeEntity.getIndex(),
            attributeValueEntity.getConfidenceValue(),
            maturityLevel);
    }
}
