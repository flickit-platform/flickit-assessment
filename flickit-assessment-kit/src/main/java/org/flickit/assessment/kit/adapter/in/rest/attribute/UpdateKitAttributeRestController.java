package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateKitAttributeUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateKitAttributeUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateKitAttributeRestController {

    private final UpdateKitAttributeUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessment-kits/{kitVersionId}/attributes/{attributeId}")
    public ResponseEntity<Void> updateKitAttribute(
        @PathVariable("kitVersionId") Long kitVersionId,
        @PathVariable("attributeId") Long attributeId,
        @RequestBody UpdateKitAttributeRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var param = toParam(kitVersionId, attributeId, requestDto, currentUserId);
        useCase.updateKitAttribute(param);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId,
                          Long attributeId,
                          UpdateKitAttributeRequestDto requestDto,
                          UUID currentUserId) {
        return new Param(kitVersionId,
            attributeId,
            requestDto.code(),
            requestDto.title(),
            requestDto.description(),
            requestDto.subjectId(),
            requestDto.index(),
            requestDto.weight(),
            currentUserId);
    }
}
