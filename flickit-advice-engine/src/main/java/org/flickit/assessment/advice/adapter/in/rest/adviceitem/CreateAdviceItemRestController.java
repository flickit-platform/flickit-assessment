package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.CreateAdviceItemUseCase;
import org.flickit.assessment.advice.application.port.in.adviceitem.CreateAdviceItemUseCase.*;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAdviceItemRestController {

    private final UserContext userContext;
    private final CreateAdviceItemUseCase useCase;

    @PostMapping("/assessments/{assessmentId}/advice-items")
    ResponseEntity<Result> createAdviceItem(@PathVariable("assessmentId") UUID assessmentId,
                                            @RequestBody CreateAdviceItemRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        Param param = toParam(assessmentId, requestDto, currentUserId);
        return new ResponseEntity<>(useCase.createAdviceItem(param), HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAdviceItemRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.title(), requestDto.description(), requestDto.cost(), requestDto.priority(), requestDto.impact(), currentUserId);
    }
}
