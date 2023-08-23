package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements
    CreateAssessmentPort,
    LoadAssessmentBySpacePort,
    UpdateAssessmentPort {

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
    public UpdateAssessmentPort.Result update(UpdateAssessmentPort.Param param) {
        return new UpdateAssessmentPort.Result(repository.update(
            param.id(),
            param.title(),
            param.colorId(),
            param.lastModificationTime()));
    }
}
