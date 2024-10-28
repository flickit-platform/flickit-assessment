package org.flickit.assessment.kit.adapter.in.rest.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAnswerRageListRestController {

    private final GetAnswerRangeListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/answer-ranges")
    public ResponseEntity<PaginatedResponse<AnswerRangeListItem>> getAnswerRageList(@PathVariable("kitVersionId") Long kitVersionId) {
        var currentUserId = userContext.getUser().id();

        var answerRangeList = useCase.getAnswerRangeList(toParam(kitVersionId, currentUserId));

        return new ResponseEntity<>(answerRangeList, HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UUID currentUserId) {
        return new Param(kitVersionId, currentUserId);
    }
}
