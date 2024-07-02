package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSpaceListService implements GetSpaceListUseCase {

    private final LoadSpaceListPort loadSpaceListPort;

    @Override
    public PaginatedResponse<SpaceListItem> getSpaceList(Param param) {
        var loadPortResult = loadSpaceListPort.loadSpaceList(param.getCurrentUserId(), param.getPage(), param.getSize());

        return new PaginatedResponse<>(
            mapToSpaceListItems(loadPortResult.getItems(), param.getCurrentUserId()),
            loadPortResult.getPage(),
            loadPortResult.getSize(),
            loadPortResult.getSort(),
            loadPortResult.getOrder(),
            loadPortResult.getTotal()
        );
    }

    private List<GetSpaceListUseCase.SpaceListItem> mapToSpaceListItems(List<LoadSpaceListPort.Result> items, UUID currentUserId) {
        return items.stream()
            .map(item -> new GetSpaceListUseCase.SpaceListItem(
                item.space().getId(),
                item.space().getTitle(),
                new SpaceListItem.Owner(item.space().getOwnerId(), item.ownerName(), item.space().getOwnerId().equals(currentUserId)),
                item.space().getLastModificationTime(),
                item.membersCount(),
                item.assessmentsCount()
            )).toList();
    }
}
