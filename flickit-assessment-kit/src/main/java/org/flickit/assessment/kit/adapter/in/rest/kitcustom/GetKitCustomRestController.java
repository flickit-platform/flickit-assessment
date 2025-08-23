package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomUseCase;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitCustomRestController {

    private final GetKitCustomUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-customs/{kitCustomId}")
    public ResponseEntity<Result> getKitCustom(@PathVariable("kitCustomId") Long kitCustomId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitCustom(toParam(kitCustomId, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetKitCustomUseCase.Param toParam(Long kitCustomId, UUID currentUserId) {
        return new GetKitCustomUseCase.Param(kitCustomId, currentUserId);
    }
}
