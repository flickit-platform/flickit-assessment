package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase.AttributeParam;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeOrdersUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAttributeOrdersRestController {

    private final UpdateAttributeOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/attributes-change-order")
    public ResponseEntity<Void> updateAttributeOrders(@PathVariable("kitVersionId") Long kitVersionId,
                                                      @RequestBody UpdateAttributeOrdersRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAttributeOrders(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateAttributeOrdersRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.orders().stream()
            .map(s -> new AttributeParam(s.id(), s.index()))
            .toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
