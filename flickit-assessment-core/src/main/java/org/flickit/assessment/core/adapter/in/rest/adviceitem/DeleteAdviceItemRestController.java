package org.flickit.assessment.core.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.adviceitem.DeleteAdviceItemUseCase;
import org.flickit.assessment.core.application.port.in.adviceitem.DeleteAdviceItemUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAdviceItemRestController {

    private final UserContext userContext;
    private final DeleteAdviceItemUseCase useCase;

    @DeleteMapping("/advice-items/{adviceItemId}")
    ResponseEntity<Void> deleteAdviceItem(@PathVariable("adviceItemId") UUID adviceItemId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteAdviceItem(toParam(adviceItemId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(UUID adviceItemId, UUID currentUserId) {
        return new Param(adviceItemId, currentUserId);
    }
}
