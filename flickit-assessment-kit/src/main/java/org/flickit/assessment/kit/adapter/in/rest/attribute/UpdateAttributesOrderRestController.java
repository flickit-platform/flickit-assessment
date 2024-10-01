package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAttributesOrderRestController {

    private final UpdateAttributesOrderUseCase useCase;
    private final UserContext userContext;

    @PutMapping("kit-versions/{kitVersionId}/attributes/change-orders")
    public ResponseEntity<Void> updateSubjectsOrder(@PathVariable("kitVersionId") Long kitVersionId,
                                                    @RequestBody UpdateAttributesOrderRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAttributesOrder(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateAttributesOrderRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.attributes().stream()
            .map(s -> new AttributeParam(s.id(), s.index()))
            .toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
