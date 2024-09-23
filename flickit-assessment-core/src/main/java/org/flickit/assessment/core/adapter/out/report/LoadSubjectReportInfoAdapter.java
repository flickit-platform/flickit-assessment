package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadSubjectReportInfoAdapter implements LoadSubjectReportInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final MaturityLevelJpaRepository maturityLevelJpaRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AttributeJpaRepository attributeRepository;
    private final AttributeValueJpaRepository attributeValueRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;

    @Override
    public Result load(UUID assessmentId, Long subjectId) {
        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));

        long kitVersionId = assessmentResultEntity.getKitVersionId();

        SubjectJpaEntity subjectEntity = subjectRepository.findByIdAndKitVersionId(subjectId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ID_NOT_FOUND));

        var maturityLevels = maturityLevelJpaRepository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(e -> MaturityLevelMapper.mapToDomainModel(e, null))
            .toList();

        Map<Long, MaturityLevel> idToMaturityLevelMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, Function.identity()));

        return new Result(
            buildSubject(subjectEntity, assessmentResultEntity, idToMaturityLevelMap),
            maturityLevels,
            buildAttributes(subjectEntity, assessmentResultEntity, idToMaturityLevelMap));
    }

    private SubjectReportItem buildSubject(SubjectJpaEntity subjectEntity,
                                           AssessmentResultJpaEntity assessmentResultEntity,
                                           Map<Long, MaturityLevel> idToMaturityLevelMap) {
        var svEntity = subjectValueRepo.findBySubjectIdAndAssessmentResult_Id(subjectEntity.getId(),
                assessmentResultEntity.getId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        var subjectMaturityLevel = idToMaturityLevelMap.get(svEntity.getMaturityLevelId());

        return new SubjectReportItem(subjectEntity.getId(),
            subjectEntity.getTitle(),
            subjectEntity.getDescription(),
            subjectMaturityLevel,
            svEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid()
        );
    }

    private List<SubjectAttributeReportItem> buildAttributes(SubjectJpaEntity subjectEntity,
                                                             AssessmentResultJpaEntity assessmentResultEntity,
                                                             Map<Long, MaturityLevel> idToMaturityLevelMap) {
        var attrValueEntities = attributeValueRepository.findByAssessmentResultIdAndSubjectId(
            assessmentResultEntity.getId(), subjectEntity.getId());
        var attrIdToAttValueEntity = attrValueEntities.stream()
            .collect(toMap(AttributeValueJpaEntity::getAttributeId, Function.identity()));
        List<UUID> attrValueIds = attrValueEntities.stream()
            .map(AttributeValueJpaEntity::getId)
            .toList();

        var attrMaturityScoreEntities =
            attributeMaturityScoreRepository.findByAttributeValueIdIn(attrValueIds);
        Map<UUID, List<AttributeMaturityScoreJpaEntity>> attrValueIdToAttrMaturityScoreEntities =
            attrMaturityScoreEntities.stream()
                .collect(groupingBy(AttributeMaturityScoreJpaEntity::getAttributeValueId));

        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllBySubjectIdAndKitVersionId(subjectEntity.getId(), subjectEntity.getKitVersionId());

        return attributeEntities.stream()
            .map(e -> {
                AttributeValueJpaEntity attrValueEntity = attrIdToAttValueEntity.get(e.getId());
                MaturityLevel maturityLevelEntity = idToMaturityLevelMap.get(attrValueEntity.getMaturityLevelId());

                return new SubjectAttributeReportItem(e.getId(),
                    e.getIndex(),
                    e.getTitle(),
                    e.getDescription(),
                    maturityLevelEntity,
                    buildAttrMaturityScores(idToMaturityLevelMap, attrValueIdToAttrMaturityScoreEntities.get(attrValueEntity.getId())),
                    attrValueEntity.getConfidenceValue());
            }).toList();
    }

    private List<SubjectAttributeReportItem.MaturityScore> buildAttrMaturityScores(Map<Long, MaturityLevel> idToMaturityLevelMap,
                                                                                   List<AttributeMaturityScoreJpaEntity> attrMaturityScoreEntities) {
        return attrMaturityScoreEntities.stream()
            .sorted(Comparator.comparing(AttributeMaturityScoreJpaEntity::getMaturityLevelId))
            .map(e -> {
                MaturityLevel level = idToMaturityLevelMap.get(e.getMaturityLevelId());
                return new SubjectAttributeReportItem.MaturityScore(level, e.getScore());
            }).toList();
    }
}
