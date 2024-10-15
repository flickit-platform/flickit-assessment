package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAttributeRestController {

    private final UpdateAttributeUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/attributes/{attributeId}")
    public ResponseEntity<Void> updateAttribute(
        @PathVariable("kitVersionId") Long kitVersionId,
        @PathVariable("attributeId") Long attributeId,
        @RequestBody UpdateAttributeRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var param = toParam(attributeId, kitVersionId, requestDto, currentUserId);
        useCase.updateAttribute(param);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long attributeId,
                          Long kitVersionId,
                          UpdateAttributeRequestDto requestDto,
                          UUID currentUserId) {
        return new Param(attributeId,
            kitVersionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            requestDto.weight(),
            requestDto.subjectId(),
            currentUserId);
    }
}
