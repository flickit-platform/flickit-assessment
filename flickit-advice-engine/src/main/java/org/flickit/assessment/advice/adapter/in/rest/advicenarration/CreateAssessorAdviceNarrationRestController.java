package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAssessorAdviceNarrationUseCase;
import org.flickit.assessment.common.application.domain.ID;
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
public class CreateAssessorAdviceNarrationRestController {

    private final CreateAssessorAdviceNarrationUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/advice-narration")
    public ResponseEntity<Void> createAssessorAdviceNarration(@PathVariable("assessmentId") UUID assessmentId,
                                                              @RequestBody CreateAssessorAdviceNarrationRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.createAssessorAdviceNarration(toParam(assessmentId, requestDto.assessorNarration(), currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private CreateAssessorAdviceNarrationUseCase.Param toParam(UUID assessmentId, String assessorNarration, UUID currentUserId) {
        return new CreateAssessorAdviceNarrationUseCase.Param(ID.toDomain(assessmentId), assessorNarration, ID.toDomain(currentUserId));
    }
}
