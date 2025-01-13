package org.flickit.assessment.core.adapter.in.rest.attributeinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attributeinsight.UpdateAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.in.attributeinsight.UpdateAttributeInsightUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAttributeInsightRestController {

    private final UpdateAttributeInsightUseCase useCase;
    private final UserContext userContext;

    @PutMapping("assessments/{assessmentId}/ai-report/attributes/{attributeId}")
    ResponseEntity<Void> updateAttributeInsight(
        @PathVariable UUID assessmentId,
        @PathVariable Long attributeId,
        @RequestBody UpdateAttributeInsightRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.updateAttributeInsight(toParam(assessmentId, attributeId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("assessments/{assessmentId}/attributes/{attributeId}/insight")
    public ResponseEntity<Void> createAttributeInsight(
        @PathVariable UUID assessmentId,
        @PathVariable Long attributeId,
        @RequestBody UpdateAttributeInsightRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.updateAttributeInsight(toParam(assessmentId, attributeId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long attributeId, UpdateAttributeInsightRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, attributeId, requestDto.assessorInsight(), currentUserId);
    }
}
