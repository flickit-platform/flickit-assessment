package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase.KitEditableInfo;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitEditableInfoRestController {

    private final GetKitEditableInfoUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/info")
    public ResponseEntity<KitEditableInfo> getKitEditableInfo(@PathVariable("kitId") Long kitId) {
        var currentUserId = userContext.getUser().id();
        var kitEditableInfo = useCase.getKitEditableInfo(toParam(kitId, currentUserId));
        return new ResponseEntity<>(kitEditableInfo, HttpStatus.OK);
    }

    private Param toParam(Long kitId, UUID currentUserId) {
        return new Param(kitId, currentUserId);
    }
}
