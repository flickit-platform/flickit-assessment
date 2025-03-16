package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.subjectvalue.SubjectValueMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdapter implements
    CreateSubjectValuePort,
    LoadSubjectValuePort {

    private final SubjectValueJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public List<SubjectValue> persistAll(List<Long> subjectIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND));

        List<SubjectValueJpaEntity> entities = subjectIds.stream().map(subjectId -> {
            SubjectValueJpaEntity subjectValue = SubjectValueMapper.mapToJpaEntity(subjectId);
            subjectValue.setAssessmentResult(assessmentResult);
            return subjectValue;
        }).toList();

        var persistedEntities = repository.saveAll(entities);
        Long kitVersionId = assessmentResult.getKitVersionId();
        var idToSubjectEntity = subjectRepository.findAllByIdInAndKitVersionId(subjectIds, kitVersionId).stream()
            .collect(toMap(SubjectJpaEntity::getId, Function.identity()));

        return persistedEntities.stream()
            .map(sv -> {
                SubjectJpaEntity subjectEntity = idToSubjectEntity.get(sv.getSubjectId());
                return mapToDomainModel(sv, subjectEntity);
            })
            .toList();
    }

    @Override
    public SubjectValue load(UUID assessmentResultId, long subjectId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var subjectValueWithSubjectView = repository.findBySubjectIdAndAssessmentResultId(subjectId, assessmentResult.getId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBJECT_VALUE_NOT_FOUND));
        var maturityLevelEntity = maturityLevelRepository.findByIdAndKitVersionId(subjectValueWithSubjectView.getSubjectValue().getMaturityLevelId(),
                assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND));
        var attributesEntity = attributeRepository.findAllBySubjectIdAndKitVersionId(subjectId, assessmentResult.getKitVersionId());

        return SubjectValueMapper.mapToDomainModel(subjectValueWithSubjectView, maturityLevelEntity, attributesEntity);
    }

    @Override
    public List<SubjectValue> loadAll(UUID assessmentResultId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var subjectValues = repository.findAllWithSubjectByAssessmentResultId(assessmentResultId);
        var maturityLevelIdToEntityMap = maturityLevelRepository.findAllByKitVersionId(assessmentResult.getKitVersionId())
            .stream().collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));
        var subjectIdToAttributesMap = attributeRepository.findAllByKitVersionId(assessmentResult.getKitVersionId()).stream()
            .collect(groupingBy(AttributeJpaEntity::getSubjectId));

        return subjectValues.stream()
            .map(sv -> SubjectValueMapper.mapToDomainModel(sv,
                maturityLevelIdToEntityMap.get(sv.getSubjectValue().getMaturityLevelId()),
                subjectIdToAttributesMap.get(sv.getSubject().getId())))
            .toList();
    }

    @Override
    public List<SubjectValue> loadAll(UUID assessmentResultId, Collection<Long> subjectIds) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var subjectValues = repository.findAllWithSubjectByAssessmentResultId(assessmentResultId, subjectIds);
        var maturityLevelIdToEntityMap = maturityLevelRepository.findAllByKitVersionId(assessmentResult.getKitVersionId())
            .stream().collect(toMap(MaturityLevelJpaEntity::getId, Function.identity()));
        var subjectIdToAttributesMap = attributeRepository.findAllByKitVersionId(assessmentResult.getKitVersionId()).stream()
            .collect(groupingBy(AttributeJpaEntity::getSubjectId));

        return subjectValues.stream()
            .map(sv -> SubjectValueMapper.mapToDomainModel(sv,
                maturityLevelIdToEntityMap.get(sv.getSubjectValue().getMaturityLevelId()),
                subjectIdToAttributesMap.get(sv.getSubject().getId())))
            .toList();
    }
}
