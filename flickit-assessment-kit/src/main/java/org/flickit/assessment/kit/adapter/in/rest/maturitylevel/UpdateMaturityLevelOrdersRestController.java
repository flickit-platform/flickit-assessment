package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.domain.MaturityLevelOrder;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelOrdersUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateMaturityLevelOrdersRestController {

    private final UpdateMaturityLevelOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessment-kits/{kitId}/maturity-levels/change-orders")
    ResponseEntity<Void> changeMaturityLevelOrders(@PathVariable("kitId") Long kitId,
                                                   @RequestBody List<UpdateMaturityLevelOrdersRequestDto> requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.changeOrders(toParam(kitId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitId, List<UpdateMaturityLevelOrdersRequestDto> requestDto, UUID currentUserId) {
        return new Param(kitId,
            requestDto.stream()
                .map(request -> new MaturityLevelOrder(request.id(), request.index(), request.value()))
                .toList(),
            currentUserId);
    }
}
