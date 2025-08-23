package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectOrdersUseCase.SubjectParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectOrdersRestController {

    private final UpdateSubjectOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("kit-versions/{kitVersionId}/subjects-change-order")
    public ResponseEntity<Void> updateSubjectOrders(@PathVariable("kitVersionId") Long kitVersionId,
                                                    @RequestBody UpdateSubjectOrdersRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubjectOrders(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateSubjectOrdersRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.orders().stream()
            .map(s -> new SubjectParam(s.id(), s.index()))
            .toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
