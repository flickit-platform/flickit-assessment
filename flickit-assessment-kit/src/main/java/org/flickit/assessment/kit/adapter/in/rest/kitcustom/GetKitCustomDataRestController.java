package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomDataUseCase;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomDataUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitCustomDataRestController {

    private final GetKitCustomDataUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/custom-subjects")
    public ResponseEntity<PaginatedResponse<Result>> getKitCustomData(@PathVariable("kitId") Long kitId,
                                                                      @RequestParam(value = "kitCustomId", required = false) Long kitCustomId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = userContext.getUser().id();
        var paginatedResponse = useCase.getKitCustomData(toParam(kitId, kitCustomId, currentUserId, page, size));
        return new ResponseEntity<>(paginatedResponse, HttpStatus.OK);
    }

    private GetKitCustomDataUseCase.Param toParam(Long kitId, Long kitCustomId, UUID currentUserId, int page, int size) {
        return new GetKitCustomDataUseCase.Param(kitId, kitCustomId, currentUserId, page, size);
    }
}
