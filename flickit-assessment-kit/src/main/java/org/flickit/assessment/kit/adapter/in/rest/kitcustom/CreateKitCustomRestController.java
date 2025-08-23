package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitcustom.CreateKitCustomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateKitCustomRestController {

    private final CreateKitCustomUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-customs")
    public ResponseEntity<CreateKitCustomResponseDto> createKitCustom(@RequestBody CreateKitCustomRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        long kitCustomId = useCase.createKitCustom(toParam(requestDto, currentUserId));
        return new ResponseEntity<>(new CreateKitCustomResponseDto(kitCustomId), HttpStatus.CREATED);
    }

    private static CreateKitCustomUseCase.Param toParam(CreateKitCustomRequestDto requestDto, UUID currentUserId) {
        CreateKitCustomUseCase.Param.KitCustomData customData = null;
        var customDataDto = requestDto.customData();
        if (customDataDto != null) {
            List<CreateKitCustomUseCase.Param.KitCustomData.CustomSubject> subjects =
                customDataDto.subjects() != null ? customDataDto.subjects().stream()
                    .map(e -> new CreateKitCustomUseCase.Param.KitCustomData.CustomSubject(e.id(), e.weight()))
                    .toList()
                : new ArrayList<>();

            List<CreateKitCustomUseCase.Param.KitCustomData.CustomAttribute> attributes =
                customDataDto.attributes() != null ? customDataDto.attributes().stream()
                    .map(e -> new CreateKitCustomUseCase.Param.KitCustomData.CustomAttribute(e.id(), e.weight()))
                    .toList()
                : new ArrayList<>();

            customData = new CreateKitCustomUseCase.Param.KitCustomData(subjects, attributes);
        }

        return new CreateKitCustomUseCase.Param(requestDto.kitId(), requestDto.title(), customData, currentUserId);
    }
}
