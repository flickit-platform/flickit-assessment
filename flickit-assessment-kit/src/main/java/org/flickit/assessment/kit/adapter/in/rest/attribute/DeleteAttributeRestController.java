package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.DeleteAttributeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAttributeRestController {

    private final DeleteAttributeUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}/attributes/{attributeId}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable("kitVersionId") Long kitVersionId,
                                                @PathVariable("attributeId") Long attributeId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAttribute(toParam(kitVersionId, attributeId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteAttributeUseCase.Param toParam(Long kitVersionId, Long attributeId, UUID currentUserId) {
        return new DeleteAttributeUseCase.Param(kitVersionId, attributeId, currentUserId);
    }
}
