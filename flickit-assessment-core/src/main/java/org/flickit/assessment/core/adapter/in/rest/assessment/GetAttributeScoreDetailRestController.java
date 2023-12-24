package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.GetAttributeScoreDetailUseCase;
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

    @GetMapping("/assessments/{assessmentId}/report/attributes/{attributeId}")
    public ResponseEntity<GetAttributeScoreDetailResponseDto> getAttributeScoreDetail(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId,
        @RequestParam(value = "maturityLevelId") Long maturityLevelId) {
        var response = toResponse(useCase.getAttributeScoreDetail(toParam(assessmentId, attributeId, maturityLevelId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAttributeScoreDetailUseCase.Param toParam(UUID assessmentId, Long attributeId, Long maturityLevelId) {
        return new GetAttributeScoreDetailUseCase.Param(assessmentId, attributeId, maturityLevelId);
    }

    private GetAttributeScoreDetailResponseDto toResponse(GetAttributeScoreDetailUseCase.Result result) {
        return new GetAttributeScoreDetailResponseDto(result);
    }
}
