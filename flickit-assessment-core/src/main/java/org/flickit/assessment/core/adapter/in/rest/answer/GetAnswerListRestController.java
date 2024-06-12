package org.flickit.assessment.core.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAnswerListRestController {

    private final GetAnswerListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/answers")
    ResponseEntity<PaginatedResponse<GetAnswerListUseCase.AnswerListItem>> getAnswerList(
        @PathVariable("assessmentId") UUID assessmentId,
        @RequestParam(value = "questionnaireId", required = false) // validated in the use-case param) Long questionnaireId,
            Long questionnaireId,
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page) {

        UUID currentUserId = userContext.getUser().id();
        PaginatedResponse<AnswerListItem> result = useCase.getAnswerList(toParam(assessmentId, questionnaireId, currentUserId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAnswerListUseCase.Param toParam(UUID assessmentId, Long questionnaireId, UUID currentUserId, int size, int page) {
        return new GetAnswerListUseCase.Param(assessmentId, questionnaireId, currentUserId, size, page);
    }
}
