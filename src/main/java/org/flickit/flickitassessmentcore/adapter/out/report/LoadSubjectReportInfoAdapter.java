package org.flickit.flickitassessmentcore.adapter.out.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValuePersistenceJpaAdapter;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectRestAdapter;
import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.flickitassessmentcore.application.service.exception.CalculateNotValidException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

@Slf4j
@Component
@AllArgsConstructor
public class LoadSubjectReportInfoAdapter implements LoadSubjectReportInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;

    private final MaturityLevelRestAdapter maturityLevelRestAdapter;
    private final SubjectRestAdapter subjectRestAdapter;

    private final QualityAttributeValuePersistenceJpaAdapter attributeValuePersistenceJpaAdapter;

    @Override
    public AssessmentResult load(UUID assessmentId, Long subjectId) {

        var assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));

        UUID assessmentResultId = assessmentResultEntity.getId();
        Long kitId = assessmentResultEntity.getAssessment().getAssessmentKitId();

        if (!Boolean.TRUE.equals(assessmentResultEntity.getIsValid())) {
            log.warn("The calculated result is not valid for [assessmentId={}, resultId={}].", assessmentId, assessmentResultId);
            throw new CalculateNotValidException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_VALID);
        }

        var svEntity = subjectValueRepo.findBySubjectIdAndAssessmentResult_Id(subjectId, assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        Map<Long, MaturityLevel> maturityLevels = maturityLevelRestAdapter.loadByKitId(kitId)
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));

        var attributeValues = buildAttributeValues(maturityLevels, assessmentResultId, kitId, subjectId);
        SubjectValue subjectValue = new SubjectValue(svEntity.getId(),
            new Subject(svEntity.getSubjectId()),
            attributeValues,
            findMaturityLevelById(maturityLevels, svEntity.getMaturityLevelId())
        );

        return new AssessmentResult(
            assessmentResultId,
            buildAssessment(assessmentResultEntity.getAssessment(), maturityLevels),
            List.of(subjectValue),
            findMaturityLevelById(maturityLevels, assessmentResultEntity.getMaturityLevelId()),
            assessmentResultEntity.getIsValid(),
            assessmentResultEntity.getLastModificationTime());
    }

    private List<QualityAttributeValue> buildAttributeValues(Map<Long, MaturityLevel> maturityLevels, UUID assessmentResultId, Long kitId, Long subjectId) {
        var subject = subjectRestAdapter.loadByAssessmentKitId(kitId)
            .stream()
            .filter(x -> x.getId() == subjectId)
            .toList()
            .get(0);
        Map<Long, QualityAttribute> qualityAttributeMap = subject.getQualityAttributes()
            .stream()
            .collect(toMap(QualityAttribute::getId, x -> x));
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
