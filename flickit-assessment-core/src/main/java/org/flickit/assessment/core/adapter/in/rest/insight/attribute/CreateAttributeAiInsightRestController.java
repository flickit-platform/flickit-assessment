package org.flickit.assessment.core.adapter.in.rest.insight.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.insight.attribute.CreateAttributeAiInsightUseCase;
import org.flickit.assessment.core.application.port.in.insight.attribute.CreateAttributeAiInsightUseCase.Param;
import org.flickit.assessment.core.application.port.in.insight.attribute.CreateAttributeAiInsightUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAttributeAiInsightRestController {

    private final CreateAttributeAiInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/attributes/{attributeId}/ai-insight")
    ResponseEntity<CreateAttributeAiInsightResponseDto> createAttributeAiInsight(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.createAiInsight(toParam(assessmentId, attributeId, currentUserId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, Long attributeId, UUID currentUserId) {
        return new Param(assessmentId, attributeId, currentUserId);
    }

    private CreateAttributeAiInsightResponseDto toResponseDto(Result result) {
        return new CreateAttributeAiInsightResponseDto(result.content());
    }
}
