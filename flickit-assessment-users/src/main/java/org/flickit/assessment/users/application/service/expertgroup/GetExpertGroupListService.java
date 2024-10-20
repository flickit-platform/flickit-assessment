package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupListPort;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupListPort.Result;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupListService implements GetExpertGroupListUseCase {

    private static final int SIZE_OF_MEMBERS = 5;
    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadExpertGroupListPort loadExpertGroupListPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<ExpertGroupListItem> getExpertGroupList(Param param) {

        var loadPortResult = loadExpertGroupListPort.loadExpertGroupList(
            toParam(param.getPage(), param.getSize(), param.getCurrentUserId()));

        return new PaginatedResponse<>(
            mapToExpertGroupListItems(loadPortResult.getItems(), param.getCurrentUserId()),
            loadPortResult.getPage(),
            loadPortResult.getSize(),
            loadPortResult.getSort(),
            loadPortResult.getOrder(),
            loadPortResult.getTotal()
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
                createFileDownloadLinkPort.createDownloadLink(item.picture(), EXPIRY_DURATION),
                item.publishedKitsCount(),
                item.membersCount(),
                item.members(),
                item.ownerId().equals(currentUserId)
            ))
            .toList();
    }
}
