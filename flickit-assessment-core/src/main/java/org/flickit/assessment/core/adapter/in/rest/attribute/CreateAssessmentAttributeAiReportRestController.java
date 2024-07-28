package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportRestController {

    private final CreateAssessmentAttributeAiReportUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessments/{assessmentId}/ai-report/attributes/{attributeId}")
    ResponseEntity<CreateAssessmentAttributeAiReportResponseDto> createAttributeAiReport(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId,
        @RequestBody CreateAssessmentAttributeAiReportRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.createAttributeAiReport(toParam(assessmentId, attributeId, requestDto.fileLink(), currentUserId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long attributeId, String fileLink, UUID currentUserId) {
        return new Param(assessmentId, attributeId, fileLink, currentUserId);
    }

    private CreateAssessmentAttributeAiReportResponseDto toResponseDto(Result result) {
        return new CreateAssessmentAttributeAiReportResponseDto(result.content());
    }
}
