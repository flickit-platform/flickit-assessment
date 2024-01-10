package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupListPort.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupListService implements GetExpertGroupListUseCase {

    private final LoadExpertGroupListPort loadExpertGroupListPort;
    private static final int SIZE_OF_MEMBERS =5;

    @Override
    public PaginatedResponse<ExpertGroupListItem> getExpertGroupList(Param param) {

        var portResult = loadExpertGroupListPort.loadExpertGroupList(
            toParam(param.getPage(), param.getSize(), param.getCurrentUserId()));

        return new PaginatedResponse<>(
            mapToExpertGroupListItems(portResult.getItems(), param.getCurrentUserId()),
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private LoadExpertGroupListPort.Param toParam(int page, int size, UUID currentUserId) {
        return new LoadExpertGroupListPort.Param(page, size, currentUserId, SIZE_OF_MEMBERS);
    }

    private List<ExpertGroupListItem> mapToExpertGroupListItems(List<Result> items, UUID currentUserId) {
        return items.stream()
            .map(item -> new ExpertGroupListItem(
                item.id(),
                item.title(),
                item.bio(),
                item.picture(),
                item.publishedKitsCount(),
                item.membersCount(),
                item.members(),
                item.ownerId().equals(currentUserId)
            ))
            .toList();
    }
}
