package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ExportKitDslUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ExportKitDslUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExportKitDslRestController {

    private final ExportKitDslUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/dsl")
    ResponseEntity<Result> exportKitDsl(@PathVariable Long kitId) {
        Result result = useCase.export(toParam(kitId, userContext.getUser().id()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(Long kitId, UUID currentUserId) {
        return new Param(kitId, currentUserId);
    }
}
