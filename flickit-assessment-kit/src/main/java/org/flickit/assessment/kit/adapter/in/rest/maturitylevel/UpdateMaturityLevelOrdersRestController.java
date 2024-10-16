package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase.MaturityLevelParam;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateMaturityLevelOrdersRestController {

    private final UpdateMaturityLevelOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/maturity-levels-change-order")
    ResponseEntity<Void> changeMaturityLevelOrders(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @RequestBody UpdateMaturityLevelOrdersRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.changeOrders(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateMaturityLevelOrdersRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.orders().stream()
            .map(request -> new MaturityLevelParam(request.id(), request.index()))
            .toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
