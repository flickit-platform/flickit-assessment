package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase.Param;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueReportFileUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAttributeValueReportFileRestController {

    private final CreateAttributeValueReportFileUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/export-report/attributes/{attributeId}")
    public ResponseEntity<CreateAttributeValueReportFileResponseDto> createAttributeValueReportFile(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.createAttributeValueReportFile(toParam(assessmentId, attributeId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long attributeId, UUID currentUserId) {
        return new Param(assessmentId, attributeId, currentUserId);
    }

    private CreateAttributeValueReportFileResponseDto toResponse(Result result) {
        return new CreateAttributeValueReportFileResponseDto(result.downloadLink());
    }
}
