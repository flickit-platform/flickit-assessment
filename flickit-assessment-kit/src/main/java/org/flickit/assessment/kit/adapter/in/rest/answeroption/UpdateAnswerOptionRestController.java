package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answeroption.UpdateAnswerOptionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAnswerOptionRestController {

    private final UpdateAnswerOptionUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/answer-options/{answerOptionId}")
    public ResponseEntity<Void> updateAnswerOption(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @PathVariable("answerOptionId") Long answerOptionId,
                                                   @RequestBody AnswerOptionRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAnswerOption(toParam(kitVersionId, answerOptionId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateAnswerOptionUseCase.Param toParam(Long kitVersionId,
                                                    Long answerOptionId,
                                                    UUID currentUserId,
                                                    AnswerOptionRequestDto dto) {
        return new UpdateAnswerOptionUseCase.Param(kitVersionId,
            answerOptionId,
            dto.index(),
            dto.title(),
            currentUserId);
    }
}
