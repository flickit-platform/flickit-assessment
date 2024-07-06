package org.flickit.assessment.core.adapter.in.rest.answerhistory;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.answerhistory.GetAnswerHistoryListUseCase;
import org.flickit.assessment.core.application.port.in.answerhistory.GetAnswerHistoryListUseCase.AnswerHistoryListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAnswerHistoryListRestController {

    private final GetAnswerHistoryListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/question/{questionId}/answer-history")
    public ResponseEntity<PaginatedResponse<AnswerHistoryListItem>> getAnswerHistoryList(
        @PathVariable("assessmentId")UUID assessmentId,
        @PathVariable("questionId") Long questionId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {

        UUID currentUserId = userContext.getUser().id();
        var answerHistoryList = useCase.getAnswerHistoryList(toParam(assessmentId, questionId, currentUserId, size, page));
        return new ResponseEntity<>(answerHistoryList, HttpStatus.OK);
    }

    private GetAnswerHistoryListUseCase.Param toParam(UUID assessmentId,
                                                      Long questionId,
                                                      UUID currentUserId,
                                                      int size,
                                                      int page) {
        return new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page);
    }
}
