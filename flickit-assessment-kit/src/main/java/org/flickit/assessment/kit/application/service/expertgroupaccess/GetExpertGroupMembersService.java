package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.flickit.assessment.kit.application.port.out.expertgroup.CheckExpertGroupExistsPort;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_EXPERT_GROUP_MEMBERS_EXPERT_GROUP_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupMembersService implements GetExpertGroupMembersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);
    private final CheckExpertGroupExistsPort checkExpertGroupExistsPort;
    private final LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;



    @Override
    public PaginatedResponse<Member> getExpertGroupMembers(Param param) {
        if (!checkExpertGroupExistsPort.existsById(param.getId()))
            throw new ResourceNotFoundException(GET_EXPERT_GROUP_MEMBERS_EXPERT_GROUP_NOT_FOUND);

        var portResult = loadExpertGroupMembersPort.loadExpertGroupMembers(param.getId(), param.getPage(), param.getSize());

        UUID ownerId = loadExpertGroupOwnerPort.loadOwnerId(param.getId())
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));

        boolean userIsOwner = ownerId.equals(param.getCurrentUserId());

        var members = mapToMembers(portResult.getItems(), userIsOwner);

        return new PaginatedResponse<>(
            members,
            portResult.getPage(),
            portResult.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private List<Member> mapToMembers(List<LoadExpertGroupMembersPort.Member> items, boolean userIsOwner) {
        return items.stream()
            .map(item -> new Member(
                item.id(),
                userIsOwner ? item.email() : null,
                item.displayName(),
                item.bio(),
                createFileDownloadLinkPort.createDownloadLink(item.picture(), EXPIRY_DURATION),
                item.linkedin()
            ))
            .toList();
    }
}
