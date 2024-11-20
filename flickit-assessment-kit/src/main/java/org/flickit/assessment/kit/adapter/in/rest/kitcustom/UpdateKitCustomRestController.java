package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitcustom.UpdateKitCustomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateKitCustomRestController {

    private final UpdateKitCustomUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessment-kits/{kitId}/kit-customs/{kitCustomId}")
    public ResponseEntity<Void> updateKitCustom(@PathVariable("kitId") Long kitId,
                                                @PathVariable("kitCustomId") Long kitCustomId,
                                                @RequestBody UpdateKitCustomRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateKitCustom(toParam(kitCustomId, kitId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static UpdateKitCustomUseCase.Param toParam(Long kitCustomId,
                                                        Long kitId,
                                                        UUID currentUserId,
                                                        UpdateKitCustomRequestDto requestDto) {
        UpdateKitCustomUseCase.Param.KitCustomData customData = null;
        var customDataDto = requestDto.customData();
        if (customDataDto != null) {
            List<UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject> subjects =
                customDataDto.subjects() != null ? customDataDto.subjects().stream()
                    .map(e -> new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(e.id(), e.weight()))
                    .toList()
                    : new ArrayList<>();

            List<UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute> attributes =
                customDataDto.attributes() != null ? customDataDto.attributes().stream()
                    .map(e -> new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(e.id(), e.weight()))
                    .toList()
                    : new ArrayList<>();

            customData = new UpdateKitCustomUseCase.Param.KitCustomData(subjects, attributes);
        }

        return new UpdateKitCustomUseCase.Param(kitCustomId, kitId, requestDto.title(), customData, currentUserId);
    }
}
