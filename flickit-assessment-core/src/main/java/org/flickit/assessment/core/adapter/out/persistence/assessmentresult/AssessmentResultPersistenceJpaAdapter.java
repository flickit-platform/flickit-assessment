package org.flickit.assessment.core.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.attributevalue.AttributeValueMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit.AssessmentKitMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.adapter.out.persistence.subjectvalue.SubjectValueMapper;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.assessmentresult.*;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdapter implements
    InvalidateAssessmentResultCalculatePort,
    InvalidateAssessmentResultConfidencePort,
    CreateAssessmentResultPort,
    LoadAssessmentResultPort,
    UpdateAssessmentResultPort {

    private final AssessmentResultJpaRepository repository;
    private final AssessmentJpaRepository assessmentRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AssessmentKitJpaRepository kitRepository;
    private final SubjectValueJpaRepository subjectValueRepository;
    private final AttributeValueJpaRepository attributeValueRepository;

    @Override
    public void invalidateCalculate(UUID assessmentResultId) {
        repository.invalidateCalculateById(assessmentResultId);
    }

    @Override
    public void invalidateConfidence(UUID assessmentResultId) {
        repository.invalidateConfidenceById(assessmentResultId);
    }

    @Override
    public UUID persist(Param param) {
        AssessmentResultJpaEntity entity = AssessmentResultMapper.mapToJpaEntity(param);
        AssessmentJpaEntity assessment = assessmentRepository.findById(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ASSESSMENT_RESULT_ASSESSMENT_ID_NOT_FOUND));
        entity.setAssessment(assessment);
        AssessmentResultJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getId();
    }

    @Override
    public Optional<AssessmentResult> loadByAssessmentId(UUID assessmentId) {
        var entity = repository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId);
        if (entity.isEmpty())
            return Optional.empty();
        MaturityLevel maturityLevel = null;
        var maturityLevelId = entity.get().getMaturityLevelId();
        if (maturityLevelId != null) {
            var maturityLevelEntity = maturityLevelRepository.findByIdAndKitVersionId(maturityLevelId, entity.get().getKitVersionId());
            maturityLevel = maturityLevelEntity.map(maturityLevelJpaEntity ->
                MaturityLevelMapper.mapToDomainModel(maturityLevelJpaEntity, null)).orElse(null);
        }
        var kit = kitRepository.findById(entity.get().getAssessment().getAssessmentKitId())
            .map(x -> AssessmentKitMapper.mapToDomainModel(x, null))
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_KIT_NOT_FOUND));

        var assessmentResult = AssessmentResultMapper.mapToDomainModel(entity.get(), maturityLevel, kit);
        var subjectValues = createSubjectValues(entity.get().getId());
        assessmentResult.setSubjectValues(subjectValues);
        return Optional.of(assessmentResult);
    }

    private List<SubjectValue> createSubjectValues(UUID assessmentResultId) {
        var subjectValueViews = subjectValueRepository.findAllWithSubjectByAssessmentResultId(assessmentResultId);
        var attributeValueViews = attributeValueRepository.findAllWithAttributeByAssessmentResultId(assessmentResultId);
        var subjectIdToAttributeValuesMap = attributeValueViews.stream()
            .collect(Collectors.groupingBy(e -> e.getAttribute().getSubjectId()));
        return subjectValueViews.stream()
            .map(v -> {
                var subjectValue = SubjectValueMapper.mapToDomainModel(v.getSubjectValue(), v.getSubject());
                List<AttributeValue> attributeValues = List.of();
                if (subjectIdToAttributeValuesMap.get(v.getSubject().getId()) != null) {
                    attributeValues = subjectIdToAttributeValuesMap.get(v.getSubject().getId()).stream()
                        .map(av -> AttributeValueMapper.mapToDomainModel(av.getAttributeValue(), av.getAttribute()))
                        .toList();
                }
                subjectValue.setAttributeValues(attributeValues);
                return subjectValue;
            }).toList();
    }

    @Override
    public void updateKitVersionId(UUID assessmentResultId, Long kitVersionId) {
        repository.updateKitVersionId(assessmentResultId, kitVersionId);
    }
}

