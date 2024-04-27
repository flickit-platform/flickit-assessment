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
        var loadPortResult = loadSpaceListPort.loadSpaceList(
            toParam(param.getSize(), param.getPage(), param.getCurrentUserId()));

        return new PaginatedResponse<>(
            mapToSpaceListItems(loadPortResult.getItems(), param.getCurrentUserId()),
            loadPortResult.getPage(),
            loadPortResult.getSize(),
            loadPortResult.getSort(),
            loadPortResult.getOrder(),
            loadPortResult.getTotal()
        );
    }

    private LoadSpaceListPort.Param toParam(int size, int page, UUID currentUserId) {
        return new LoadSpaceListPort.Param(size, page, currentUserId);
    }

    private List<GetSpaceListUseCase.SpaceListItem> mapToSpaceListItems(List<LoadSpaceListPort.Result> items, UUID currentUserId) {
        return items.stream()
            .map(item -> new GetSpaceListUseCase.SpaceListItem(
                item.id(),
                item.code(),
                item.title(),
                item.ownerId().equals(currentUserId),
                item.lastModificationTime(),
                item.membersCount(),
                item.assessmentsCount()
            )).toList();
    }
}
