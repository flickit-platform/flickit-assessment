package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdapter implements
    CreateSubjectValuePort {

    private final SubjectValueJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final SubjectInsightJpaRepository subjectInsightRepository;

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
            .collect(Collectors.toMap(SubjectJpaEntity::getId, Function.identity()));

        return persistedEntities.stream()
            .map(sv -> {
                SubjectJpaEntity subjectEntity = idToSubjectEntity.get(sv.getSubjectId());
                return SubjectValueMapper.mapToDomainModel(sv, subjectEntity);
            })
            .toList();
    }
}
