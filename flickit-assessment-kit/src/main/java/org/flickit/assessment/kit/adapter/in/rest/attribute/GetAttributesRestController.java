package org.flickit.assessment.kit.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.Param;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.SubjectListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributesRestController {

    private final GetAttributesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/attributes")
    public ResponseEntity<PaginatedResponse<SubjectListItem>> getAttributes(@PathVariable("kitVersionId") Long kitVersionId,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getAttributes(toParam(kitVersionId, page, size, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, int page, int size, UUID currentUserId) {
        return new Param(kitVersionId, page, size, currentUserId);
    }

}
