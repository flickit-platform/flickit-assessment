package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionRestController {

    private final UpdateQuestionUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessment-kits/{kitId}/questions/{questionId}")
    public ResponseEntity<Void> updateQuestion(@PathVariable("kitId") Long kitId,
                                               @PathVariable("questionId") Long questionId,
                                               @RequestParam UpdateQuestionRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateQuestion(toParam(kitId, questionId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private UpdateQuestionUseCase.Param toParam(Long kitId, Long questionId, UUID currentUserId, UpdateQuestionRequestDto requestDto) {
        return new UpdateQuestionUseCase.Param(kitId,
            questionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.hint(),
            requestDto.mayNotBeApplicable(),
            requestDto.advisable(),
            currentUserId);
    }
}
