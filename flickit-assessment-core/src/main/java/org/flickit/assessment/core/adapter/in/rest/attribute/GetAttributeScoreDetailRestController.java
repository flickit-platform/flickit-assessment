package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributeScoreDetailRestController {

    private final GetAttributeScoreDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/report/attributes/{attributeId}")
    public ResponseEntity<GetAttributeScoreDetailResponseDto> getAttributeScoreDetail(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId,
        @RequestParam(value = "maturityLevelId", required = false) Long maturityLevelId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.getAttributeScoreDetail(toParam(assessmentId, attributeId, maturityLevelId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAttributeScoreDetailUseCase.Param toParam(UUID assessmentId, Long attributeId, Long maturityLevelId, UUID currentUserId) {
        return new GetAttributeScoreDetailUseCase.Param(assessmentId, attributeId, maturityLevelId, currentUserId);
    }

    private GetAttributeScoreDetailResponseDto toResponse(GetAttributeScoreDetailUseCase.Result result) {
        return new GetAttributeScoreDetailResponseDto(
            result.maxPossibleScore(),
            result.gainedScore(),
            result.gainedScorePercentage(),
            result.questionsCount(),
            result.questionnaires());
    }
}
