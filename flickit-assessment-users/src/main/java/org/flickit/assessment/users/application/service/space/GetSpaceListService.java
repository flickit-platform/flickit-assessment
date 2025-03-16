package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;
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
        var portResult = loadSpaceListPort.loadSpaceList(param.getCurrentUserId(), param.getPage(), param.getSize());

        return new PaginatedResponse<>(
            mapToSpaceListItems(portResult.getItems(), param.getCurrentUserId()),
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private List<GetSpaceListUseCase.SpaceListItem> mapToSpaceListItems(List<LoadSpaceListPort.Result> items, UUID currentUserId) {
        return items.stream()
            .map(item -> {
                var space = item.space();
                return new GetSpaceListUseCase.SpaceListItem(
                    space.getId(),
                    space.getTitle(),
                    new SpaceListItem.Owner(space.getOwnerId(), item.ownerName(), space.getOwnerId().equals(currentUserId)),
                    new SpaceListItem.Type(space.getType().getCode(), space.getType().getTitle()),
                    SpaceStatus.ACTIVE.equals(space.getStatus()),
                    space.getLastModificationTime(),
                    item.membersCount(),
                    item.assessmentsCount()
                );
            })
            .toList();
    }
}
