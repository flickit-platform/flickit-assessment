package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuesPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.adapter.out.persistence.subjectvalue.SubjectValueMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdapter implements
    CreateSubjectValuePort,
    LoadSubjectValuesPort {

    private final SubjectValueJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final SubjectValueJpaRepository subjectValueRepository;
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
    public List<SubjectValue> loadByAssessmentId(UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
        var subjectValuesEntities = subjectValueRepository.findByAssessmentResultId(assessmentResult.getId());
        var subjectEntities = subjectRepository.findAllByKitVersionIdOrderByIndex(assessmentResult.getKitVersionId());
        var subjectIdToValueMap = subjectValuesEntities.stream()
            .collect(toMap(SubjectValueJpaEntity::getSubjectId, Function.identity()));
        var subjectIdToAttributeMap = attributeRepository.findAllByKitVersionId(assessmentResult.getKitVersionId())
            .stream().collect(groupingBy(AttributeJpaEntity::getSubjectId));

        return subjectEntities.stream()
            .map(s -> mapToDomainModel(subjectIdToValueMap.get(s.getId()), s, subjectIdToAttributeMap.get(s.getId())))
            .toList();
    }
}
