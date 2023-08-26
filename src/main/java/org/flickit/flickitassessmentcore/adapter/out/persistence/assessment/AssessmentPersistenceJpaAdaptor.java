package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentsWithMaturityLevelIdBySpacePort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdaptor implements
    CreateAssessmentPort,
    LoadAssessmentsWithMaturityLevelIdBySpacePort {

    private final AssessmentJpaRepository repository;

    @Override
    public UUID persist(Param param) {
        AssessmentJpaEntity unsavedEntity = AssessmentMapper.mapCreateParamToJpaEntity(param);
        AssessmentJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public List<AssessmentListItem> loadAssessmentListItemBySpaceId(Long spaceId, int page, int size) {
        return repository.findBySpaceIdOrderByLastModificationTimeDesc(spaceId, PageRequest.of(page, size)).stream()
            .map(AssessmentMapper::mapToAssessmentListItem)
            .toList();
    }
}
