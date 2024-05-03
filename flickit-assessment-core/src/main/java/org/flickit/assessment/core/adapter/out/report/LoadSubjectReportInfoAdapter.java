package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.report.SubjectReport;
import org.flickit.assessment.core.application.domain.report.SubjectReport.SubjectReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReport.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReport.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.SubjectReport.AttributeReportItem.MaturityScore;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
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
    private final QualityAttributeValueJpaRepository attributeValueRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;


    @Override
    public SubjectReport load(UUID assessmentId, Long subjectId) {
        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));

        SubjectJpaEntity subjectEntity = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new ResourceNotFoundException(""));

        long kitVersionId = assessmentResultEntity.getKitVersionId();
        List<MaturityLevelJpaEntity> maturityLevelEntities = maturityLevelJpaRepository.findAllByKitVersionIdOrderByIndex(kitVersionId);
        Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevelEntities = maturityLevelEntities.stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));

        List<MaturityLevel> maturityLevels = maturityLevelEntities.stream()
            .map(this::buildMaturityLevel)
            .toList();

        return new SubjectReport(
            buildSubject(subjectEntity, assessmentResultEntity, maturityLevelIdToMaturityLevelEntities),
            maturityLevels,
            buildAttributes(subjectEntity, assessmentResultEntity, maturityLevelIdToMaturityLevelEntities));

    }

    private SubjectReportItem buildSubject(SubjectJpaEntity subjectEntity,
                                                 AssessmentResultJpaEntity assessmentResultEntity,
                                                 Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevelEntities) {

        var svEntity = subjectValueRepo.findBySubjectRefNumAndAssessmentResult_Id(subjectEntity.getRefNum(),
                assessmentResultEntity.getId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        MaturityLevelJpaEntity subjectMaturityLevelEntity =
            findMaturityLevelById(maturityLevelIdToMaturityLevelEntities, svEntity.getMaturityLevelId());

        return new SubjectReportItem(subjectEntity.getId(),
            subjectEntity.getTitle(),
            buildMaturityLevel(subjectMaturityLevelEntity),
            svEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid()
        );
    }

    private List<AttributeReportItem> buildAttributes(SubjectJpaEntity subjectEntity,
                                                      AssessmentResultJpaEntity assessmentResultEntity,
                                                      Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevelEntities) {
        List<QualityAttributeValueJpaEntity> attrValueEntities =
            attributeValueRepository.findByAssessmentResultIdAndSubjectId(assessmentResultEntity.getId(), subjectEntity.getId());
        Map<UUID, QualityAttributeValueJpaEntity> attrRefNumToAttValueEntity = attrValueEntities.stream()
            .collect(toMap(QualityAttributeValueJpaEntity::getAttributeRefNum, Function.identity()));
        List<UUID> attrValueIds = attrValueEntities.stream()
            .map(QualityAttributeValueJpaEntity::getId)
            .toList();


        List<AttributeMaturityScoreJpaEntity> attrMaturityScoreEntities =
            attributeMaturityScoreRepository.findByAttributeValueIdIn(attrValueIds);
        Map<UUID, List<AttributeMaturityScoreJpaEntity>> attrValueIdToAttrMaturityScoreEntities =
            attrMaturityScoreEntities.stream()
                .collect(groupingBy(AttributeMaturityScoreJpaEntity::getAttributeValueId));

        return subjectEntity.getAttributes().stream()
            .map(e -> {
                QualityAttributeValueJpaEntity attrValueEntity = attrRefNumToAttValueEntity.get(e.getRefNum());
                MaturityLevelJpaEntity maturityLevelEntity =
                    findMaturityLevelById(maturityLevelIdToMaturityLevelEntities, attrValueEntity.getMaturityLevelId());

                return new AttributeReportItem(e.getId(),
                    e.getIndex(),
                    e.getTitle(),
                    e.getDescription(),
                    buildMaturityLevel(maturityLevelEntity),
                    buildAttrMaturityScores(maturityLevelIdToMaturityLevelEntities,
                        attrValueIdToAttrMaturityScoreEntities,
                        attrValueEntity),
                    attrValueEntity.getConfidenceValue());
            }).toList();
    }

    private List<MaturityScore> buildAttrMaturityScores(Map<Long, MaturityLevelJpaEntity> maturityLevelIdToMaturityLevelEntities,
                                                        Map<UUID, List<AttributeMaturityScoreJpaEntity>> attrValueIdToAttrMaturityScoreEntities,
                                                        QualityAttributeValueJpaEntity attrValueEntity) {

            return attrValueIdToAttrMaturityScoreEntities.get(attrValueEntity.getId()).stream()
                .sorted(Comparator.comparing(AttributeMaturityScoreJpaEntity::getMaturityLevelId))
                .map(e -> {
                    MaturityLevelJpaEntity levelEntity =
                        findMaturityLevelById(maturityLevelIdToMaturityLevelEntities, e.getMaturityLevelId());
                    return new MaturityScore(buildMaturityLevel(levelEntity), e.getScore());
                }).toList();
    }

    private MaturityLevel buildMaturityLevel(MaturityLevelJpaEntity maturityLevelEntity) {
        return new SubjectReport.MaturityLevel(maturityLevelEntity.getId(),
            maturityLevelEntity.getTitle(),
            maturityLevelEntity.getIndex(),
            maturityLevelEntity.getValue());
    }

    private MaturityLevelJpaEntity findMaturityLevelById(Map<Long, MaturityLevelJpaEntity> maturityLevels, long id) {
        if (!maturityLevels.containsKey(id)) {
            log.error("No maturityLevel found with id={}", id);
            throw new ResourceNotFoundException(REPORT_SUBJECT_MATURITY_LEVEL_NOT_FOUND);
        }
        return maturityLevels.get(id);
    }
}
