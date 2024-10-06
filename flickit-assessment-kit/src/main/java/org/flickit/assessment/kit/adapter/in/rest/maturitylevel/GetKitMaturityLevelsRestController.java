package org.flickit.assessment.kit.adapter.in.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase.MaturityLevelListItem;
import org.flickit.assessment.kit.application.port.in.maturitylevel.GetKitMaturityLevelsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitMaturityLevelsRestController {

    private final GetKitMaturityLevelsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/maturity-levels")
    public ResponseEntity<PaginatedResponse<MaturityLevelListItem>> getKitMaturityLevels(@PathVariable("kitVersionId") Long kitVersionId,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "20") int size) {
        var currentUserId = userContext.getUser().id();
        var kitMaturityLevels = useCase.getKitMaturityLevels(toParam(kitVersionId, size, page, currentUserId));
        return new ResponseEntity<>(kitMaturityLevels, HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, Integer size, Integer page, UUID currentUserId) {
        return new Param(kitVersionId, size, page, currentUserId);
    }
}
