package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionnairesProgressRestController {

    private final GetQuestionnairesProgressUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessmentId}/questionnaires/progress")
    ResponseEntity<GetQuestionnairesProgressResponseDto> getQuestionnairesProgress(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponseDto(useCase.getQuestionnairesProgress(toParam(assessmentId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetQuestionnairesProgressResponseDto toResponseDto(Result result) {
        return new GetQuestionnairesProgressResponseDto(result.questionnairesProgress());
    }

    private GetQuestionnairesProgressUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new GetQuestionnairesProgressUseCase.Param(assessmentId, currentUserId);
    }
}
