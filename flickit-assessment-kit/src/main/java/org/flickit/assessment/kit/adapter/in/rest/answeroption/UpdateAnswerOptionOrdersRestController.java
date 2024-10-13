package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answeroptions.UpdateAnswerOptionOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.answeroptions.UpdateAnswerOptionOrdersUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAnswerOptionOrdersRestController {

    private final UpdateAnswerOptionOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("kit-versions/{kitVersionId}/answer-option-change-order")
    public ResponseEntity<Void> updateAnswerOptionOrders(@PathVariable Long kitVersionId,
                                                         @RequestBody UpdateAnswerOptionOrdersRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();

        useCase.changeOrders(toParam(kitVersionId, requestDto, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateAnswerOptionOrdersRequestDto requestDto, UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.orders().stream().map(
                request -> new UpdateAnswerOptionOrdersUseCase.AnswerOptionParam(request.id(), request.index())).toList(),
            currentUserId);
    }
}
