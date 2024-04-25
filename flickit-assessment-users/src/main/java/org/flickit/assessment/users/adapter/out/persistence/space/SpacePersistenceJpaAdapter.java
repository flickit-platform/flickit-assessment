package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements LoadSpaceListPort {

    private final SpaceJpaRepository repository;

    @Override
    public PaginatedResponse<Result> loadSpaceList(Param param) {
        var pageResult = repository.findByUserId(param.currentUserId(), PageRequest.of(param.page(), param.size()));

        List<LoadSpaceListPort.Result> items = pageResult.getContent().stream()
            .map(SpaceMapper::mapToPortResult)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            ExpertGroupAccessJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
