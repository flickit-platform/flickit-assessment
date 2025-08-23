package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionsOrderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionsOrderRestController {

    private final UpdateQuestionsOrderUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionsId}/questions-change-order")
    public ResponseEntity<Void> updateQuestionsOrder(@PathVariable("kitVersionsId") Long kitVersionsId,
                                                     @RequestBody UpdateQuestionsOrderRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateQuestionsOrder(toParam(kitVersionsId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateQuestionsOrderUseCase.Param toParam(Long kitVersionsId,
                                                      UpdateQuestionsOrderRequestDto requestDto,
                                                      UUID currentUserId) {
        var questionOrders = requestDto.questionOrders().stream()
            .map(e -> new UpdateQuestionsOrderUseCase.Param.QuestionOrder(e.questionId(), e.index()))
            .toList();
        return new UpdateQuestionsOrderUseCase.Param(kitVersionsId,
            questionOrders,
            requestDto.questionnaireId(),
            currentUserId);
    }
}
