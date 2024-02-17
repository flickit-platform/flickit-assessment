package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.AttributeEvidenceListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class GetAttributeEvidenceListRestController {

    private final GetAttributeEvidenceListUseCase useCase;

    @GetMapping("/attribute-evidences")
    ResponseEntity<PaginatedResponse<AttributeEvidenceListItem>> getAttributeEvidenceList(
        @RequestParam(value = "assessmentId", required = false) // validated in the use-case param
        UUID assessmentId,
        @RequestParam(value = "attributeId", required = false) // validated in the use-case param
        Long attributeId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page
    ) {
        var result = useCase.getAttributeEvidenceList(toParam(assessmentId, attributeId, size, page));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAttributeEvidenceListUseCase.Param toParam(UUID assessmentId,
                                                          Long attributeId,
                                                          int size,
                                                          int page) {
        return new GetAttributeEvidenceListUseCase.Param(assessmentId,
            attributeId,
            size,
            page);
    }
}
