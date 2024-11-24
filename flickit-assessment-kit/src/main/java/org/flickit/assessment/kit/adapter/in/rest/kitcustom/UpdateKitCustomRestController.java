package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateKitCustomRestController {

    private final UpdateKitCustomUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-customs/{kitCustomId}")
    public ResponseEntity<Void> updateKitCustom(@PathVariable("kitCustomId") Long kitCustomId,
                                                @RequestBody UpdateKitCustomRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateKitCustom(toParam(kitCustomId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static Param toParam(Long kitCustomId,
                                 UpdateKitCustomRequestDto requestDto,
                                 UUID currentUserId) {
        Param.KitCustomData customData = null;
        var customDataDto = requestDto.customData();
        if (customDataDto != null) {
            List<Param.KitCustomData.CustomSubject> subjects =
                customDataDto.subjects() != null ? customDataDto.subjects().stream()
                    .map(e -> new Param.KitCustomData.CustomSubject(e.id(), e.weight()))
                    .toList()
                    : new ArrayList<>();

            List<Param.KitCustomData.CustomAttribute> attributes =
                customDataDto.attributes() != null ? customDataDto.attributes().stream()
                    .map(e -> new Param.KitCustomData.CustomAttribute(e.id(), e.weight()))
                    .toList()
                    : new ArrayList<>();

            customData = new Param.KitCustomData(subjects, attributes);
        }

        return new Param(kitCustomId, requestDto.kitId(), requestDto.title(), customData, currentUserId);
    }
}
