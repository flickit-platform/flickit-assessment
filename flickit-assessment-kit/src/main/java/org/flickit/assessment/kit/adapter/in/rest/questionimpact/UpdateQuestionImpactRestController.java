package org.flickit.assessment.kit.adapter.in.rest.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionimpact.UpdateQuestionImpactUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionImpactRestController {

    private final UpdateQuestionImpactUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/question-impacts/{questionImpactId}")
    public ResponseEntity<Void> updateQuestionImpact(@PathVariable("kitVersionId") Long kitVersionId,
                                                     @PathVariable("questionImpactId") Long questionImpactId,
                                                     @RequestBody UpdateQuestionImpactRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateQuestionImpact(toParam(kitVersionId, questionImpactId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateQuestionImpactUseCase.Param toParam(Long kitVersionId,
                                                      Long questionImpactId,
                                                      UUID currentUserId,
                                                      UpdateQuestionImpactRequestDto requestDto) {
        return new UpdateQuestionImpactUseCase.Param(kitVersionId,
            questionImpactId,
            requestDto.attributedId(),
            requestDto.maturityLevelId(),
            requestDto.weight(),
            currentUserId);
    }
}
