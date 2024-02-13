package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort.Result;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.LoadExpertGroupMembersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupMembersService implements GetExpertGroupMembersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private final LoadExpertGroupMembersPort loadExpertGroupMembersPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private LoadExpertGroupMembersPort.Param toParam(int page, int size, long id) {
        return new LoadExpertGroupMembersPort.Param(page, size, id);
    }

    private List<Member> mapToMembers(List<Result> items) {
        return items.stream()
            .map(item -> new Member(
                item.id(),
                item.email(),
                item.displayNme(),
                item.bio(),
                createFileDownloadLinkPort.createDownloadLink(item.picture(), EXPIRY_DURATION),
                item.linkedin()
            ))
            .toList();
    }

    @Override
    public PaginatedResponse<Member> getExpertGroupMembers(Param param) {
        var portResult = loadExpertGroupMembersPort.loadExpertGroupMembers(
            toParam(param.getPage(), param.getSize(), param.getId()));

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
}
