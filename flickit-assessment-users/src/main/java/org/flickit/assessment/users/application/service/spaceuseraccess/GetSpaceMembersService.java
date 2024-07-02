package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.GetSpaceMembersUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.LoadSpaceMembersPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSpaceMembersService implements GetSpaceMembersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpaceMembersPort loadSpaceMembersPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<Member> getSpaceMembers(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadSpaceMembersPort.loadSpaceMembers(param.getId(), param.getPage(), param.getSize());
        var members = mapToMembers(portResult.getItems());

        return new PaginatedResponse<>(
            members,
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private List<GetSpaceMembersUseCase.Member> mapToMembers(List<LoadSpaceMembersPort.Member> items) {
        return items.stream()
            .map(item -> new GetSpaceMembersUseCase.Member(
                item.id(),
                item.email(),
                item.displayName(),
                item.bio(),
                item.isOwner(),
                createFileDownloadLinkPort.createDownloadLink(item.picture(), EXPIRY_DURATION),
                item.linkedin()
            ))
            .toList();
    }
}
