package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupMembersUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMembersPort.Result;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupMembersPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupMembersService implements GetExpertGroupMembersUseCase {

    private final LoadExpertGroupMembersPort loadExpertGroupMembersPort;

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
                item.picture(),
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
