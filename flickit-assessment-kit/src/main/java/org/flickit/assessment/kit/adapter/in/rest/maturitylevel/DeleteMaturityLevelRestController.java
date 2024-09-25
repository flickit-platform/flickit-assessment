package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.maturitylevel.DeleteMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.DeleteMaturityLevelUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteMaturityLevelRestController {

    private final DeleteMaturityLevelUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/assessment-kits/{kitId}/maturity-levels/{maturityLevelId}")
    ResponseEntity<Void> deleteMaturityLevel(@PathVariable Long kitId, @PathVariable Long maturityLevelId) {
        var currentUserId = userContext.getUser().id();
        useCase.delete(toParam(kitId, maturityLevelId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(Long kitId, Long maturityLevelId, UUID currentUserId) {
        return new Param(kitId, maturityLevelId, currentUserId);
    }
}
