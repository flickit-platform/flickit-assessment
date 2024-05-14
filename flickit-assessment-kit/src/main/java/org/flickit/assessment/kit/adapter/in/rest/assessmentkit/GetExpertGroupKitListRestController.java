package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetExpertGroupKitListUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetExpertGroupKitListUseCase.Param;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetExpertGroupKitListUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetExpertGroupKitListRestController {

    private final GetExpertGroupKitListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/expert-groups/{expertGroupId}/assessment-kits")
    public ResponseEntity<PaginatedResponse<Result>> getExpertGroupKitList(@PathVariable(value = "expertGroupId") Long expertGroupId,
                                                                           @RequestParam(defaultValue = "50") int size,
                                                                           @RequestParam(defaultValue = "0") int page) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getExpertGroupKitList(toParam(expertGroupId, page, size, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(Long expertGroupId, int page, int size, UUID currentUserId) {
        return new Param(expertGroupId, page, size, currentUserId);
    }
}
