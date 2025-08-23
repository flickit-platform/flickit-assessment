package org.flickit.assessment.kit.adapter.in.rest.kitcustom;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomSubjectUseCase;
import org.flickit.assessment.kit.application.port.in.kitcustom.GetKitCustomSubjectUseCase.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitCustomSubjectRestController {

    private final GetKitCustomSubjectUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/custom-subjects")
    public ResponseEntity<PaginatedResponse<Subject>> getKitCustomSubject(@PathVariable("kitId") Long kitId,
                                                                          @RequestParam(value = "kitCustomId", required = false) Long kitCustomId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = userContext.getUser().id();
        var paginatedResponse = useCase.getKitCustomSubject(toParam(kitId, kitCustomId, currentUserId, page, size));
        return new ResponseEntity<>(paginatedResponse, HttpStatus.OK);
    }

    private GetKitCustomSubjectUseCase.Param toParam(Long kitId, Long kitCustomId, UUID currentUserId, int page, int size) {
        return new GetKitCustomSubjectUseCase.Param(kitId, kitCustomId, currentUserId, page, size);
    }
}
