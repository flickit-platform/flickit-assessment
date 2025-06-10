package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase;
import org.flickit.assessment.users.application.port.in.space.GetTopSpacesUseCase.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetTopSpacesRestController {

    private final GetTopSpacesUseCase getTopSpacesUseCase;
    private final UserContext userContext;

    @GetMapping("/top-spaces")
    public GetTopSpacesResponseDto getTopSpaces() {
        UUID currentUserId = userContext.getUser().id();
        var result = getTopSpacesUseCase.getSpaceList(new Param(currentUserId));
        return toResponseDto(result.items());
    }

    private GetTopSpacesResponseDto toResponseDto(List<Result.SpaceListItem> items) {
        var result = items.stream()
            .map(r -> new GetTopSpacesResponseDto.SpaceListItem(r.id(),
                r.title(),
                GetTopSpacesResponseDto.SpaceListItem.Type.of(r.type()),
                r.isDefault()))
            .toList();

        return new GetTopSpacesResponseDto(result);
    }
}
