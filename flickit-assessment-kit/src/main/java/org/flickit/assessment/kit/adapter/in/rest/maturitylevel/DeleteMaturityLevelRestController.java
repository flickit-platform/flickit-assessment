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

    @DeleteMapping("/kit-versions/{kitVersionId}/maturity-levels/{maturityLevelId}")
    public ResponseEntity<Void> deleteMaturityLevel(@PathVariable("kitVersionId") Long kitId, @PathVariable("maturityLevelId") Long maturityLevelId) {
        var currentUserId = userContext.getUser().id();
        useCase.delete(toParam(maturityLevelId, kitId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(Long maturityLevelId, Long kitVersionId, UUID currentUserId) {
        return new Param(maturityLevelId, kitVersionId, currentUserId);
    }
}
