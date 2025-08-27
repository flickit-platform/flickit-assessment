package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.adapter.in.rest.space.GetTopSpacesResponseDto.SpaceListItemDto;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetTopSpacesRestController {

    private final GetTopSpacesUseCase getTopSpacesUseCase;
    private final UserContext userContext;

    @GetMapping("/top-spaces")
    public ResponseEntity<GetTopSpacesResponseDto> getTopSpaces() {
        UUID currentUserId = userContext.getUser().id();
        var result = getTopSpacesUseCase.getSpaceList(new Param(currentUserId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private GetTopSpacesResponseDto toResponseDto(GetTopSpacesUseCase.Result result) {
        var itemDtos = result.items().stream()
            .map(r -> new SpaceListItemDto(r.id(),
                r.title(),
                SpaceListItemDto.TypeDto.of(r.type()),
                r.selected(),
                r.isDefault()))
            .toList();

        return new GetTopSpacesResponseDto(itemDtos);
    }
}
