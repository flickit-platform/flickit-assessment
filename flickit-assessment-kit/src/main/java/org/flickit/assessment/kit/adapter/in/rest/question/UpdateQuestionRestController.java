package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionRestController {

    private final UpdateQuestionUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/questions/{questionId}")
    public ResponseEntity<Void> updateQuestion(@PathVariable("kitVersionId") Long kitVersionId,
                                               @PathVariable("questionId") Long questionId,
                                               @RequestBody UpdateQuestionRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateQuestion(toParam(kitVersionId, questionId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateQuestionUseCase.Param toParam(Long kitVersionId,
                                                Long questionId,
                                                UUID currentUserId,
                                                UpdateQuestionRequestDto requestDto) {
        return new UpdateQuestionUseCase.Param(kitVersionId,
            questionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.hint(),
            requestDto.mayNotBeApplicable(),
            requestDto.advisable(),
            requestDto.answerRangeId(),
            currentUserId);
    }
}
