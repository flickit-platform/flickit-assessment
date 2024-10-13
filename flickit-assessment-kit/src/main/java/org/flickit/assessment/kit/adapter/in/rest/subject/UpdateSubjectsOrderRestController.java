package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.Param;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectsOrderRestController {

    private final UpdateSubjectsOrderUseCase useCase;
    private final UserContext userContext;

    @PutMapping("kit-versions/{kitVersionId}/subjects-change-order")
    public ResponseEntity<Void> updateSubjectsOrder(@PathVariable("kitVersionId") Long kitVersionId,
                                                    @RequestBody UpdateSubjectsOrderRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubjectsOrder(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateSubjectsOrderRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.subjects().stream()
            .map(s -> new SubjectParam(s.id(), s.index()))
            .toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
