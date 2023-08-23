package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.UPDATE_ASSESSMENT_ASSESSMENT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements
    CreateAssessmentPort,
    LoadAssessmentBySpacePort,
    LoadAssessmentPort,
    SaveAssessmentPort {

    private final AssessmentJpaRepository repository;

    @Override
    public UUID persist(CreateAssessmentPort.Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public List<Assessment> loadAssessmentBySpaceId(Long spaceId, int page, int size) {
        return repository.findBySpaceIdOrderByLastModificationTimeDesc(spaceId, PageRequest.of(page, size)).stream()
            .map(AssessmentMapper::mapToDomainModel)
            .collect(Collectors.toList());
    }

    @Override
    public LoadAssessmentPort.Result loadAssessment(UUID id) {
        Optional<AssessmentJpaEntity> jpaEntity = repository.findById(id);
        if (jpaEntity.isPresent()) {
            Assessment assessment = AssessmentMapper.mapToDomainModel(jpaEntity.get());
            return new LoadAssessmentPort.Result(assessment);
        } else {
            throw new ResourceNotFoundException(UPDATE_ASSESSMENT_ASSESSMENT_NOT_FOUND);
        }
    }

    @Override
    public SaveAssessmentPort.Result saveAssessment(SaveAssessmentPort.Param param) {
        return new SaveAssessmentPort.Result(repository.save(AssessmentMapper.mapToJpaEntity(param.assessment())).getId());
    }
}
