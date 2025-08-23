package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase.Param;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireOrdersUseCase.QuestionnaireParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateQuestionnaireOrdersRestController {

    private final UpdateQuestionnaireOrdersUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/questionnaires-change-order")
    ResponseEntity<Void> changeMaturityLevelOrders(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @RequestBody UpdateQuestionnaireOrdersRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.changeOrders(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UpdateQuestionnaireOrdersRequestDto requestDto, UUID currentUserId) {
        var orders = requestDto.orders().stream().map(
            request -> new QuestionnaireParam(request.id(), request.index())).toList();
        return new Param(kitVersionId, orders, currentUserId);
    }
}
