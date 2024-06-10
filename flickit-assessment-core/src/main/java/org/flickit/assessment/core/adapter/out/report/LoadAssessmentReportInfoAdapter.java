package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.minio.MinioAdapter;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.SubjectRefNumAttributeValueView;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
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
import static org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadAssessmentReportInfoAdapter implements LoadAssessmentReportInfoPort {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final AssessmentJpaRepository assessmentRepository;
    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final AssessmentKitJpaRepository assessmentKitJpaRepository;
    private final ExpertGroupJpaRepository expertGroupJpaRepository;
    private final MaturityLevelJpaRepository maturityLevelJpaRepository;
    private final SubjectJpaRepository subjectJpaRepository;
    private final AttributeValueJpaRepository attributeValueJpaRepository;
    private final MinioAdapter minioAdapter;

    @Override
    public Result load(UUID assessmentId) {
        if (!assessmentRepository.existsByIdAndDeletedFalse(assessmentId))
            throw new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND);

        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        AssessmentJpaEntity assessment = assessmentResultEntity.getAssessment();
        AssessmentKitJpaEntity assessmentKitEntity = assessmentKitJpaRepository.findById(assessment.getAssessmentKitId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND));

        ExpertGroupJpaEntity expertGroupEntity = expertGroupJpaRepository.findById(assessmentKitEntity.getExpertGroupId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_EXPERT_GROUP_NOT_FOUND));

        long kitVersionId = assessmentResultEntity.getKitVersionId();
        var maturityLevelEntities = maturityLevelJpaRepository.findAllByKitVersionIdOrderByIndex(kitVersionId);

        Map<Long, MaturityLevel> idToMaturityLevel = maturityLevelEntities.stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, e -> mapToDomainModel(e, null)));

        AssessmentReportItem assessmentReportItem = new AssessmentReportItem(assessmentId,
            assessment.getTitle(),
            buildAssessmentKitItem(expertGroupEntity, assessmentKitEntity, idToMaturityLevel.values().stream().toList()),
            idToMaturityLevel.get(assessmentResultEntity.getMaturityLevelId()),
            assessmentResultEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid(),
            AssessmentColor.valueOfById(assessment.getColorId()),
            assessment.getCreationTime(),
            assessment.getLastModificationTime());

        UUID assessmentResultId = assessmentResultEntity.getId();
        List<AssessmentSubjectReportItem> subjects = buildSubjectReportItems(assessmentResultId, idToMaturityLevel);

        return new Result(assessmentReportItem, subjects);
    }

    private AssessmentReportItem.AssessmentKitItem buildAssessmentKitItem(ExpertGroupJpaEntity expertGroupEntity,
                                                                          AssessmentKitJpaEntity assessmentKitEntity,
                                                                          List<MaturityLevel> maturityLevels) {
        AssessmentReportItem.AssessmentKitItem.ExpertGroup expertGroup =
            new AssessmentReportItem.AssessmentKitItem.ExpertGroup(expertGroupEntity.getId(),
                expertGroupEntity.getTitle(),
                minioAdapter.createDownloadLink(expertGroupEntity.getPicture(), EXPIRY_DURATION));

        return new AssessmentReportItem.AssessmentKitItem(assessmentKitEntity.getId(),
            assessmentKitEntity.getTitle(),
            assessmentKitEntity.getSummary(),
            maturityLevels.size(),
            maturityLevels,
            expertGroup);
    }

    private List<AssessmentSubjectReportItem> buildSubjectReportItems(UUID assessmentResultId,
                                                                      Map<Long, MaturityLevel> idToMaturityLevel) {
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResultId);
        Set<UUID> refNums = subjectValueEntities.stream()
            .map(SubjectValueJpaEntity::getSubjectRefNum)
            .collect(Collectors.toSet());

        var subjectRefNumToSubjectValue = subjectValueEntities.stream()
            .collect(toMap(SubjectValueJpaEntity::getSubjectRefNum, Function.identity()));

        var subjectRefNumToAttributeValueMap = attributeValueJpaRepository.findByAssessmentResultIdAndSubjectRefNumIn(
            assessmentResultId, refNums)
            .stream()
            .collect(groupingBy(SubjectRefNumAttributeValueView::getSubjectRefNum));

        List<SubjectJpaEntity> subjectEntities = subjectJpaRepository.findAllByRefNumIn(refNums);

        return subjectEntities.stream()
            .map(e -> {
                Long maturityLevelId = subjectRefNumToSubjectValue.get(e.getRefNum()).getMaturityLevelId();
                MaturityLevel subjectMaturityLevel = idToMaturityLevel.get(maturityLevelId);
                return new AssessmentSubjectReportItem(e.getId(),
                    e.getTitle(),
                    e.getIndex(),
                    e.getDescription(),
                    subjectRefNumToSubjectValue.get(e.getRefNum()).getConfidenceValue(),
                    subjectMaturityLevel,
                    subjectRefNumToAttributeValueMap.get(e.getRefNum()).stream()
                        .map(x -> buildAttributeReportItem(idToMaturityLevel, x))
                        .toList());
            }).toList();
    }

    private AttributeReportItem buildAttributeReportItem(Map<Long, MaturityLevel> idToMaturityLevel,
                                                         SubjectRefNumAttributeValueView attributeValueView) {
        var attribute = attributeValueView.getAttribute();
        var attributeValue = attributeValueView.getAttributeValue();
        var maturityLevel = idToMaturityLevel.get(attributeValue.getMaturityLevelId());
        return new AttributeReportItem(attribute.getId(),
            attribute.getTitle(),
            attribute.getDescription(),
            attribute.getIndex(),
            attributeValue.getConfidenceValue(),
            maturityLevel);
    }
}
