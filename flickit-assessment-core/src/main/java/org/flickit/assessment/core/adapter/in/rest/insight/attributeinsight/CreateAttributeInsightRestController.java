package org.flickit.assessment.core.adapter.in.rest.insight.attributeinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.insight.attributeinsight.CreateAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.in.insight.attributeinsight.CreateAttributeInsightUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAttributeInsightRestController {

    private final CreateAttributeInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/attributes/{attributeId}/insight")
    public ResponseEntity<Void> createAttributeInsight(@PathVariable UUID assessmentId,
                                                       @PathVariable Long attributeId,
                                                       @RequestBody CreateAttributeInsightRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.createAttributeInsight(toParam(assessmentId, attributeId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, Long attributeId, CreateAttributeInsightRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, attributeId, requestDto.assessorInsight(), currentUserId);
    }
}
