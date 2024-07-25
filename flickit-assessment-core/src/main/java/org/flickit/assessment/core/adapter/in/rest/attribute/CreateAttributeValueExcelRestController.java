package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.CreateAttributeValueExcelUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAttributeValueExcelRestController {

    private final CreateAttributeValueExcelUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/report-excel/attributes/{attributeId}")
    public ResponseEntity<CreateAttributeValueExcelResponseDto> getAttributeScoreDetail(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.createAttributeValueExcel(toParam(assessmentId, attributeId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private CreateAttributeValueExcelUseCase.Param toParam(UUID assessmentId, Long attributeId, UUID currentUserId) {
        return new CreateAttributeValueExcelUseCase.Param(assessmentId, attributeId, currentUserId);
    }

    private CreateAttributeValueExcelResponseDto toResponse(CreateAttributeValueExcelUseCase.Result result) {
        return new CreateAttributeValueExcelResponseDto(result.downloadLink());
    }
}
