package org.flickit.assessment.core.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_ASSESSMENT_MATURITY_LEVEL_NOT_FOUND;

@Slf4j
@Component
@AllArgsConstructor
public class LoadAssessmentReportInfoAdapter implements LoadAssessmentReportInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;

    @Override
    public AssessmentResult load(UUID assessmentId) {
        AssessmentResultJpaEntity assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));

        UUID assessmentResultId = assessmentResultEntity.getId();
        long kitVersionId = assessmentResultEntity.getKitVersionId();
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResultId);

        Map<Long, MaturityLevel> maturityLevels = maturityLevelJpaAdapter.loadByKitVersionIdWithCompetences(kitVersionId)
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));
        List<SubjectValue> subjectValues = buildSubjectValues(subjectValueEntities, maturityLevels);

        return new AssessmentResult(
            assessmentResultId,
            buildAssessment(assessmentResultEntity.getAssessment(), kitVersionId, maturityLevels),
            kitVersionId,
            subjectValues,
            findMaturityLevelById(maturityLevels, assessmentResultEntity.getMaturityLevelId()),
            assessmentResultEntity.getConfidenceValue(),
            assessmentResultEntity.getIsCalculateValid(),
            assessmentResultEntity.getIsConfidenceValid(),
            assessmentResultEntity.getLastModificationTime(),
            assessmentResultEntity.getLastCalculationTime(),
            assessmentResultEntity.getLastConfidenceCalculationTime());
    }

    private List<SubjectValue> buildSubjectValues(List<SubjectValueJpaEntity> subjectValueEntities, Map<Long, MaturityLevel> maturityLevels) {
        return subjectValueEntities.stream()
            .map(x ->
                new SubjectValue(
                    x.getId(),
                    new Subject(x.getSubjectId(), null, null),
                    null,
                    findMaturityLevelById(maturityLevels, x.getMaturityLevelId()),
                    x.getConfidenceValue())
            ).toList();
    }

    private Assessment buildAssessment(AssessmentJpaEntity assessmentEntity, long kitVersionId, Map<Long, MaturityLevel> maturityLevels) {
        AssessmentKit kit = new AssessmentKit(assessmentEntity.getAssessmentKitId(), kitVersionId, new ArrayList<>(maturityLevels.values()));
        return mapToDomainModel(assessmentEntity, kit);
    }

    private MaturityLevel findMaturityLevelById(Map<Long, MaturityLevel> maturityLevels, long id) {
        if (!maturityLevels.containsKey(id)) {
            log.error("No maturityLevel found with id={}", id);
            throw new ResourceNotFoundException(REPORT_ASSESSMENT_MATURITY_LEVEL_NOT_FOUND);
        }
        return maturityLevels.get(id);
    }
}
