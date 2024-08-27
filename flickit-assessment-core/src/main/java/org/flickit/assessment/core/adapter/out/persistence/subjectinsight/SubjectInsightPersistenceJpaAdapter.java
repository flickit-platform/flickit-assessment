package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectDefaultInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class SubjectInsightPersistenceJpaAdapter implements
        LoadSubjectInsightPort,
        CreateSubjectInsightPort,
        UpdateSubjectInsightPort,
        LoadSubjectDefaultInsightPort {

    private final SubjectInsightJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;
    private final SubjectValueJpaRepository subjectValueRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public Optional<SubjectInsight> load(UUID assessmentResultId, Long subjectId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        return repository.findByAssessmentResultIdAndSubjectId(assessmentResultId, subjectId)
                .map(x -> {
                    boolean isValid = x.getInsightTime().isAfter(assessmentResult.getLastCalculationTime());
                    return SubjectInsightMapper.mapToDomainModel(x, isValid);
                });
    }

    @Override
    public void persist(SubjectInsight subjectInsight) {
        repository.save(SubjectInsightMapper.mapToJpaEntity(subjectInsight));
    }

    @Override
    public void update(SubjectInsight subjectInsight) {
        repository.updateByAssessmentResultIdAndSubjectId(subjectInsight.getAssessmentResultId(),
                subjectInsight.getSubjectId(),
                subjectInsight.getInsight(),
                subjectInsight.getInsightTime(),
                subjectInsight.getInsightBy());
    }

    @Override
    public String loadDefaultInsight(UUID assessmentResultId, long subjectId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
                .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var subject = subjectRepository.findByIdAndKitVersionId(subjectId, assessmentResult.getKitVersionId())
                .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_INSIGHT_SUBJECT_ID_NOT_FOUND));
        var subjectValue = subjectValueRepository.findBySubjectIdAndAssessmentResult_Id(subjectId, assessmentResultId)
                .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_INSIGHT_SUBJECT_VALUE_NOT_FOUND));
        var maturityLevel = maturityLevelRepository.findByIdAndKitVersionId(subjectValue.getMaturityLevelId(), assessmentResult.getKitVersionId())
                .orElseThrow(() -> new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND));
        var maturityLevels = maturityLevelRepository.findAllByKitVersionIdOrderByIndex(assessmentResult.getKitVersionId());
        var attributes = attributeRepository.findAllBySubjectIdAndKitVersionId(subjectId, assessmentResult.getKitVersionId());

        return MessageBundle.message(GET_SUBJECT_INSIGHT_SUBJECT_INSIGHT_DEFAULT_TEMPLATE,
                subject.getTitle(),
                subject.getDescription(),
                subjectValue.getConfidenceValue(),
                maturityLevel.getValue(),
                maturityLevels.size(),
                maturityLevel.getTitle(),
                attributes.size(),
                subject.getTitle());
    }
}
