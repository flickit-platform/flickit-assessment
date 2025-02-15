package org.flickit.assessment.core.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.answer.ApproveAnswerUseCase;
import org.flickit.assessment.core.application.port.in.answer.ApproveAnswerUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApproveAnswerRestController {

    private final ApproveAnswerUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/approve-answer")
    public ResponseEntity<Void> approveAnswer(@PathVariable("assessmentId") UUID assessmentId,
                                             @RequestBody ApproveAnswerRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.approveAnswer(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, ApproveAnswerRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.questionId(), currentUserId);
    }
}
