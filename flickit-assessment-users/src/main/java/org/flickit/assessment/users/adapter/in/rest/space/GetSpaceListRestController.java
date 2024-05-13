package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.GetSpaceListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSpaceListRestController {

    private final GetSpaceListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/spaces")
    public ResponseEntity<PaginatedResponse<GetSpaceListUseCase.SpaceListItem>> getSpaceList(
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {

        var currentUserId = userContext.getUser().id();
        var spaceList = useCase.getSpaceList(toParam(size, page, currentUserId));
        return new ResponseEntity<>(spaceList, HttpStatus.OK);
    }

    private GetSpaceListUseCase.Param toParam(int size, int page, UUID currentUserId) {
        return new GetSpaceListUseCase.Param(size, page, currentUserId);
    }
}
