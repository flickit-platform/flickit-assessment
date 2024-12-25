package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.ApproveAttributeInsightUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApproveAttributeInsightRestController {

    private final ApproveAttributeInsightUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/attributes/{attributeId}/approve-insight")
    public ResponseEntity<Void> approveAttributeInsight(@PathVariable("assessmentId")UUID assessmentId,
                                                        @PathVariable("attributeId") Long attributeId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.approveAttributeInsight(toParam(assessmentId, attributeId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ApproveAttributeInsightUseCase.Param toParam(UUID assessmentId, Long attributeId, UUID currentUserId) {
        return new ApproveAttributeInsightUseCase.Param(assessmentId, attributeId, currentUserId);
    }
}
