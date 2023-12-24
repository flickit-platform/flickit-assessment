package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.adapter.out.persistence.kit.qualityattribute.QualityAttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue.QualityAttributeValuePersistenceJpaAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.exception.CalculateNotValidException;
import org.flickit.assessment.core.application.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJoinAttributeView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public AssessmentResult load(UUID assessmentId, Long subjectId) {

        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));

        UUID assessmentResultId = assessmentResultEntity.getId();
        Long kitId = assessmentResultEntity.getAssessment().getAssessmentKitId();

        if (!Boolean.TRUE.equals(assessmentResultEntity.getIsCalculateValid())) {
            log.warn("The calculated result is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResultId);
            throw new CalculateNotValidException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_VALID);
        }

        if (!Boolean.TRUE.equals(assessmentResultEntity.getIsConfidenceValid())) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResultId);
            throw new ConfidenceCalculationNotValidException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_VALID);
        }

        var svEntity = subjectValueRepo.findBySubjectIdAndAssessmentResult_Id(subjectId, assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        Map<Long, MaturityLevel> maturityLevels = maturityLevelJpaAdapter.loadByKitId(kitId)
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));

        var attributeValues = buildAttributeValues(maturityLevels, assessmentResultId, kitId, subjectId);
        SubjectValue subjectValue = new SubjectValue(svEntity.getId(),
            new Subject(svEntity.getSubjectId()),
            attributeValues,
            findMaturityLevelById(maturityLevels, svEntity.getMaturityLevelId()),
            svEntity.getConfidenceValue()
        );

        return new AssessmentResult(
            assessmentResultId,
            buildAssessment(assessmentResultEntity.getAssessment(), maturityLevels),
            List.of(subjectValue),
            findMaturityLevelById(maturityLevels, assessmentResultEntity.getMaturityLevelId()),
            assessmentResultEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid(),
            assessmentResultEntity.getLastModificationTime());
    }

    private List<QualityAttributeValue> buildAttributeValues(Map<Long, MaturityLevel> maturityLevels, UUID assessmentResultId, Long kitId, Long subjectId) {
        List<SubjectJoinAttributeView> subjectJoinAttributes = subjectRepository.loadByAssessmentKitId(kitId);
        Map<Long, QualityAttribute> qualityAttributeMap = subjectJoinAttributes
            .stream()
            .filter(x -> Objects.equals(x.getSubject().getId(), subjectId))
            .collect(toMap(x -> x.getAttribute().getId(), x -> QualityAttributeMapper.mapToDomainModel(x.getAttribute())));
        return attributeValuePersistenceJpaAdapter.loadAttributeValues(assessmentResultId, maturityLevels)
            .stream()
            .filter(x -> qualityAttributeMap.containsKey(x.getQualityAttribute().getId()))
            .toList();
    }

    private Assessment buildAssessment(AssessmentJpaEntity assessmentEntity, Map<Long, MaturityLevel> maturityLevels) {
        AssessmentKit kit = new AssessmentKit(assessmentEntity.getAssessmentKitId(), new ArrayList<>(maturityLevels.values()));
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
