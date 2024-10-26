package org.flickit.assessment.kit.adapter.in.rest.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionimpact.DeleteQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.in.questionimpact.DeleteQuestionImpactUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteQuestionImpactRestController {

    private final DeleteQuestionImpactUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}/question-impacts/{questionImpactId}")
    public ResponseEntity<Void> deleteQuestionImpact(@PathVariable("kitVersionId") Long kitVersionId, @PathVariable("questionImpactId") long questionImpactId) {
        var currentUserId = userContext.getUser().id();
        useCase.delete(toParam(kitVersionId, questionImpactId, currentUserId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(Long kitVersionId, long questionImpactId, UUID currentUserId) {
        return new Param(questionImpactId, kitVersionId, currentUserId);
    }
}
