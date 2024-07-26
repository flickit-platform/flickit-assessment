package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentAttributeAiReportUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentAttributeAiReportUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentAttributeAiReportRestController {

    private final CreateAssessmentAttributeAiReportUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{id}/ai-report")
    ResponseEntity<CreateAssessmentAttributeAiReportResponseDto> createAssessmentAttributeAiReportRestController(
        @PathVariable("id") UUID id,
        @RequestBody CreateAssessmentAttributeAiReportRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.create(toParam(id, requestDto.fileLink(), currentUserId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, String fileLink, UUID currentUserId) {
        return new Param(assessmentId, fileLink, currentUserId);
    }

    private CreateAssessmentAttributeAiReportResponseDto toResponseDto(Result result) {
        return new CreateAssessmentAttributeAiReportResponseDto(result.content());
    }
}
