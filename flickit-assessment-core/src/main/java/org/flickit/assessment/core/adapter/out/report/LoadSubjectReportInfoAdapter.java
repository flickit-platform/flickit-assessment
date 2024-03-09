package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue.QualityAttributeValuePersistenceJpaAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadSubjectReportInfoAdapter implements LoadSubjectReportInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final SubjectJpaRepository subjectRepository;

    private final MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;
    private final QualityAttributeValuePersistenceJpaAdapter attributeValuePersistenceJpaAdapter;

    @Override
    public AssessmentResult load(UUID assessmentId, UUID subjectRefNum) {

        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));

        UUID assessmentResultId = assessmentResultEntity.getId();
        Long kitId = assessmentResultEntity.getAssessment().getAssessmentKitId();
        long kitVersionId = assessmentResultEntity.getKitVersionId();

        var svEntity = subjectValueRepo.findBySubjectRefNumAndAssessmentResult_Id(subjectRefNum, assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        Map<Long, MaturityLevel> maturityLevels = maturityLevelJpaAdapter.loadByKitId(kitId)
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));

        var attributeValues = attributeValuePersistenceJpaAdapter.loadBySubjectRefNum(assessmentResultId, subjectRefNum, maturityLevels);
        SubjectValue subjectValue = buildSubjectValue(svEntity, attributeValues, maturityLevels);

        return new AssessmentResult(
            assessmentResultId,
            buildAssessment(assessmentResultEntity.getAssessment(), kitVersionId, maturityLevels),
            kitVersionId,
            List.of(subjectValue),
            findMaturityLevelById(maturityLevels, assessmentResultEntity.getMaturityLevelId()),
            assessmentResultEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid(),
            assessmentResultEntity.getLastModificationTime(),
            assessmentResultEntity.getLastCalculationTime(),
            assessmentResultEntity.getLastConfidenceCalculationTime());
    }

    @NotNull
    private SubjectValue buildSubjectValue(SubjectValueJpaEntity svEntity, List<QualityAttributeValue> attributeValues, Map<Long, MaturityLevel> maturityLevels) {
        SubjectJpaEntity subjectEntity = subjectRepository.findByRefNum(svEntity.getSubjectRefNum());
        return new SubjectValue(svEntity.getId(),
            new Subject(subjectEntity.getId(), svEntity.getSubjectRefNum(), null),
            attributeValues,
            findMaturityLevelById(maturityLevels, svEntity.getMaturityLevelId()),
            svEntity.getConfidenceValue()
        );
    }

    private Assessment buildAssessment(AssessmentJpaEntity assessmentEntity, long kitVersionId, Map<Long, MaturityLevel> maturityLevels) {
        AssessmentKit kit = new AssessmentKit(assessmentEntity.getAssessmentKitId(), kitVersionId, new ArrayList<>(maturityLevels.values()));
        return mapToDomainModel(assessmentEntity, kit);
    }

    private MaturityLevel findMaturityLevelById(Map<Long, MaturityLevel> maturityLevels, long id) {
        if (!maturityLevels.containsKey(id)) {
            log.error("No maturityLevel found with id={}", id);
            throw new ResourceNotFoundException(REPORT_SUBJECT_MATURITY_LEVEL_NOT_FOUND);
        }
        return maturityLevels.get(id);
    }
}
