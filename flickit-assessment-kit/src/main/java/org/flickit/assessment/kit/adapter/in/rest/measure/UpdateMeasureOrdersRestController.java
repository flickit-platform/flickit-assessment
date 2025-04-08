package org.flickit.assessment.kit.adapter.in.rest.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureOrdersUseCase.Param;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureOrdersUseCase.MeasureParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateMeasureOrdersRestController {

    private final UpdateMeasureOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/measures-change-order")
    ResponseEntity<Void> changeMaturityLevelOrders(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @RequestBody UpdateMeasureOrdersRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.changeOrders(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateMeasureOrdersRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.orders().stream().map(
            request -> new MeasureParam(request.id(), request.index())).toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
