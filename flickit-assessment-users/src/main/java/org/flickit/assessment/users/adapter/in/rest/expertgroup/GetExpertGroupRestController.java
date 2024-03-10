package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetExpertGroupRestController {

    private final GetExpertGroupUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/expert-groups/{id}")
    public ResponseEntity<GetExpertGroupResponseDto> getExpertGroup(@PathVariable("id") Long id) {
        var currentUserId = userContext.getUser().id();
        GetExpertGroupUseCase.Result result = useCase.getExpertGroup(toParam(id, currentUserId));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private GetExpertGroupUseCase.Param toParam(long id, UUID currentUserId) {
        return new GetExpertGroupUseCase.Param(id, currentUserId);
    }

    private GetExpertGroupResponseDto toResponse(GetExpertGroupUseCase.Result result) {
        return new GetExpertGroupResponseDto(
            result.expertGroup().getId(),
            result.expertGroup().getTitle(),
            result.expertGroup().getBio(),
            result.expertGroup().getAbout(),
            result.pictureLink(),
            result.expertGroup().getWebsite(),
            result.editable()
        );
    }
}
