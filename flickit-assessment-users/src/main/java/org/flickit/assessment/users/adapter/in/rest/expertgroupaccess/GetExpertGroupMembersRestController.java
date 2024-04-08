package org.flickit.assessment.users.adapter.in.rest.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.GetExpertGroupMembersUseCase.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetExpertGroupMembersRestController {

    private final GetExpertGroupMembersUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/expert-groups/{id}/members")
    public ResponseEntity<PaginatedResponse<Member>> getExpertGroupMembers(
        @PathVariable("id") long id,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        var currentUserId = userContext.getUser().id();
        var memberPaginatedResponse = useCase.getExpertGroupMembers(toParam(id, status, currentUserId, size, page));
        return new ResponseEntity<>(memberPaginatedResponse, HttpStatus.OK);
    }

    private GetExpertGroupMembersUseCase.Param toParam(long expertGroupId, String status, UUID currentUserId, int size, int page) {
        return new GetExpertGroupMembersUseCase.Param(expertGroupId, status, currentUserId, size, page);
    }
}
