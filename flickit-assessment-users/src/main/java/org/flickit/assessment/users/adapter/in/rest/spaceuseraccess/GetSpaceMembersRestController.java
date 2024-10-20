package org.flickit.assessment.users.adapter.in.rest.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.GetSpaceMembersUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSpaceMembersRestController {

    private final GetSpaceMembersUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/spaces/{id}/members")
    public ResponseEntity<PaginatedResponse<GetSpaceMembersUseCase.Member>> getSpaceMembers(
        @PathVariable("id") long id,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        var currentUserId = userContext.getUser().id();
        var memberPaginatedResponse = useCase.getSpaceMembers(toParam(id, currentUserId, size, page));
        return new ResponseEntity<>(memberPaginatedResponse, HttpStatus.OK);
    }

    private GetSpaceMembersUseCase.Param toParam(long spaceId, UUID currentUserId, int size, int page) {
        return new GetSpaceMembersUseCase.Param(spaceId, currentUserId, size, page);
    }
}
