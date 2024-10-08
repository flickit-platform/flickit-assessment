package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitUserListService implements GetKitUserListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadKitUsersPort loadKitUsersPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<UserListItem> getKitUserList(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!Objects.equals(expertGroup.getOwnerId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitUsersPaginatedResponse = loadKitUsersPort.loadKitUsers(toParam(param.getKitId(), param.getPage(), param.getSize()));
        List<UserListItem> items = kitUsersPaginatedResponse.getItems().stream()
            .map(e -> {
                String pictureLink = null;
                if (e.picturePath() != null && !e.picturePath().trim().isBlank()) {
                    pictureLink = createFileDownloadLinkPort.createDownloadLink(e.picturePath(), EXPIRY_DURATION);
                }
                return new UserListItem(e.id(),
                    e.displayName(),
                    e.email(),
                    pictureLink,
                    !expertGroup.getOwnerId().equals(e.id()));
            }).toList();

        return new PaginatedResponse<>(items,
            kitUsersPaginatedResponse.getPage(),
            kitUsersPaginatedResponse.getSize(),
            kitUsersPaginatedResponse.getSort(),
            kitUsersPaginatedResponse.getOrder(),
            kitUsersPaginatedResponse.getTotal());
    }

    private LoadKitUsersPort.Param toParam(Long kitId, int page, int size) {
        return new LoadKitUsersPort.Param(kitId, page, size);
    }
}
