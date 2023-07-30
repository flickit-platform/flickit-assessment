package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.SoftDeleteAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.flickitassessmentcore.application.service.assessment.CreateAssessmentService.NOT_DELETED_DELETION_TIME;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REMOVE_ASSESSMENT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements CreateAssessmentPort, LoadAssessmentBySpacePort, SoftDeleteAssessmentPort {

    private final AssessmentJpaRepository repository;

    @Override
    public UUID persist(CreateAssessmentPort.Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public List<Assessment> loadAssessmentBySpaceId(Long spaceId, int page, int size) {
        return repository.findBySpaceIdAndDeletionTimeOrderByLastModificationDateDesc(spaceId, NOT_DELETED_DELETION_TIME, PageRequest.of(page, size)).stream()
            .map(AssessmentMapper::mapToDomainModel)
            .collect(Collectors.toList());
    }

    @Override
    public void softDeleteAndSetDeletionTimeById(SoftDeleteAssessmentPort.Param param) {
        AssessmentJpaEntity entity = repository.findById(param.id())
            .orElseThrow(()->new ResourceNotFoundException(REMOVE_ASSESSMENT_ID_NOT_FOUND));
        entity.setDeletionTime(param.deletionTime());
        repository.save(entity);
    }
}
