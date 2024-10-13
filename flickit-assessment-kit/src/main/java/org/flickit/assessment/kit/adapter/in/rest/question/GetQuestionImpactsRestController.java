package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionImpactsUseCase;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionImpactsUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionImpactsRestController {

    private final GetQuestionImpactsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("kit-versions/{kitVersionId}/questions/{questionId}/impacts")
    public ResponseEntity<Result> getQuestionImpacts(@PathVariable Long kitVersionId,
                                                     @PathVariable Long questionId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getQuestionImpacts(toParam(kitVersionId, questionId, currentUserId));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, Long questionId, UUID currentUserId) {
        return new Param(questionId, kitVersionId, currentUserId);
    }
}
