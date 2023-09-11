package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueBySubjectIdPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdaptor implements
    CreateSubjectValuePort,
    LoadSubjectValueBySubjectIdPort {

    private final SubjectValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAll(List<Long> subjectIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId).get();

        List<SubjectValueJpaEntity> entities = subjectIds.stream().map(subjectId -> {
            SubjectValueJpaEntity subjectValue = SubjectValueMapper.mapToJpaEntity(subjectId);
            subjectValue.setAssessmentResult(assessmentResult);
            return subjectValue;
        }).toList();

        repository.saveAll(entities);
    }


    @Override
    public SubjectValue load(Long subjectId) {
        SubjectValueJpaEntity entity = repository.findBySubjectIdOrderByLastModificationTimeDesc(subjectId).get(0);
        return SubjectValueMapper.mapToDomainModel(entity);
    }
}
