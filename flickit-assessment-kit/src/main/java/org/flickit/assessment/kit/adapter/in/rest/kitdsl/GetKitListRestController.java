package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitListUseCase.KitListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitListRestController {

    private final GetKitListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits")
    public ResponseEntity<PaginatedResponse<KitListItem>> getKitList(
        @RequestParam(required = false) Boolean isPrivate, // validated in the use-case param)
        @RequestParam(defaultValue = "50") int size,
        @RequestParam(defaultValue = "0") int page
    ) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitList(toParam(isPrivate, page, size, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetKitListUseCase.Param toParam(Boolean isPrivate, int page, int size, UUID currentUserId) {
        return new GetKitListUseCase.Param(isPrivate, page, size, currentUserId);
    }
}
