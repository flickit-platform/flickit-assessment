package org.flickit.assessment.users.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
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
            portResult.getItems().stream()
                .map(e -> mapToSpaceListItems(e, param.getCurrentUserId()))
                .toList(),
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private GetSpaceListUseCase.SpaceListItem mapToSpaceListItems(LoadSpaceListPort.Result item, UUID currentUserId) {
        var spaceType = SpaceType.valueOfById(item.spaceType());
        return new GetSpaceListUseCase.SpaceListItem(
            item.space().getId(),
            item.space().getTitle(),
            new SpaceListItem.Owner(item.space().getOwnerId(), item.ownerName(), item.space().getOwnerId().equals(currentUserId)),
            new SpaceListItem.Type(Objects.requireNonNull(spaceType).getCode(), spaceType.getTitle()),
            item.space().getLastModificationTime(),
            item.membersCount(),
            item.assessmentsCount()
        );
    }
}
