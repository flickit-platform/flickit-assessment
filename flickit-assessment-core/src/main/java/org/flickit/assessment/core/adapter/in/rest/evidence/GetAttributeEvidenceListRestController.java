package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.AttributeEvidenceListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributeEvidenceListRestController {

    private final GetAttributeEvidenceListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/attributes/{attributeId}/evidences")
    ResponseEntity<PaginatedResponse<AttributeEvidenceListItem>> getAttributeEvidenceList(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId,
        @RequestParam(value = "type" , required = false) String type, //validated in use case
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page
    ) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getAttributeEvidenceList(toParam(assessmentId, attributeId, type, currentUserId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAttributeEvidenceListUseCase.Param toParam(UUID assessmentId, Long attributeId, String type,
                                                          UUID currentUserId, int size, int page) {
        return new GetAttributeEvidenceListUseCase.Param(assessmentId, attributeId, type, currentUserId, size, page);
    }
}
