package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.GetSpaceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSpaceRestController {

    private final GetSpaceUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/space/{id}")
    public ResponseEntity<GetSpaceResponseDto> getSpace(@PathVariable("id") Long id) {
        var currentUserId = userContext.getUser().id();
        var space = useCase.getSpace(toParam(id, currentUserId));
        return new ResponseEntity<>(toResponse(space), HttpStatus.OK);
    }

    private GetSpaceUseCase.Param toParam(long id, UUID currentUserId) {
        return new GetSpaceUseCase.Param(id, currentUserId);
    }

    private GetSpaceResponseDto toResponse(GetSpaceUseCase.Result result) {
        return new GetSpaceResponseDto(
            result.id(),
            result.code(),
            result.title(),
            result.isOwner(),
            result.lastModificationTime(),
            result.membersCount(),
            result.assessmentsCount()
        );
    }
}
