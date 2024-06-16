package org.flickit.assessment.core.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdapter implements
    CreateSubjectValuePort {

    private final SubjectValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

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

        return persistedEntities.stream()
            .map(SubjectValueMapper::mapToDomainModel)
            .toList();
    }
}
