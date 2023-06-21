package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements CreateAssessmentPort, LoadAssessmentBySpacePort {

    private final AssessmentJpaRepository repository;

    @Override
    public UUID persist(Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public List<Assessment> loadAssessmentBySpaceId(Long spaceId) {
        return repository.loadAssessmentBySpaceId(spaceId).stream()
            .map(AssessmentMapper::mapToDomainModel)
            .collect(Collectors.toList());
    }
}
