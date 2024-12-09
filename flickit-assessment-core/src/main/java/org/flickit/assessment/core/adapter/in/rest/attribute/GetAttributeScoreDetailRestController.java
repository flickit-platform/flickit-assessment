package org.flickit.assessment.core.adapter.in.rest.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.*;
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
    public ResponseEntity<PaginatedResponse<QuestionScore>> getAttributeScoreDetail(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId,
        @RequestParam(value = "maturityLevelId", required = false) Long maturityLevelId,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "order", required = false) String order,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "page", required = false) Integer page) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getAttributeScoreDetail(toParam(assessmentId, attributeId, maturityLevelId, sort, order, size, page, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Long attributeId, Long maturityLevelId,
                          String sort, String order, Integer size, Integer page, UUID currentUserId) {
        return new Param(assessmentId,
            attributeId,
            maturityLevelId,
            sort,
            order,
            size,
            page,
            currentUserId);
    }
}
