package org.flickit.assessment.kit.adapter.in.rest.answeroption;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answeroption.GetQuestionOptionsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionOptionsRestController {

    private final GetQuestionOptionsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/questions/{questionId}/options")
    public ResponseEntity<GetQuestionOptionsUseCase.Result> getQuestionOptions(@PathVariable("kitVersionId") Long kitVersionId,
                                                                               @PathVariable("questionId") Long questionId) {
        UUID currentUserId = userContext.getUser().id();
        GetQuestionOptionsUseCase.Result options = useCase.getQuestionOptions(toParam(kitVersionId, questionId, currentUserId));
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    private GetQuestionOptionsUseCase.Param toParam(Long kitVersionId, Long questionId, UUID currentUserId) {
        return new GetQuestionOptionsUseCase.Param(kitVersionId, questionId, currentUserId);
    }
}
