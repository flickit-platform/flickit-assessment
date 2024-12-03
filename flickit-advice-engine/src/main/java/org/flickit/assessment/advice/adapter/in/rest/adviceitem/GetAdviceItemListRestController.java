package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemListUseCase.*;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemListUseCase;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAdviceItemListRestController {

    private final UserContext userContext;
    private final GetAdviceItemListUseCase useCase;

    @GetMapping("/advice-items")
    ResponseEntity<PaginatedResponse<AdviceItemListItem>> getAdviceItemList(
        @RequestParam(value = "assessmentId", required = false) UUID assessmentId, // validated in the use-case param
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        Param param = toParam(assessmentId, size, page, currentUserId);
        return new ResponseEntity<>(useCase.getAdviceItems(param), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, int size, int page, UUID currentUserId) {
        return new Param(assessmentId, size, page, currentUserId);
    }
}
