package org.flickit.assessment.core.adapter.in.rest.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentInsightRestController {

    private final CreateAssessmentInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessments/{assessment_id}/insight")
    ResponseEntity<CreateAssessmentInsightResponseDto> createAssessmentInsight(@PathVariable("assessment_id") UUID assessmentId,
                                                                               @RequestBody CreateAssessmentInsightRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.createAssessmentInsight(toParam(assessmentId, requestDto, currentUserId));

        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAssessmentInsightRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.insight(), currentUserId);
    }

    private CreateAssessmentInsightResponseDto toResponseDto(Result result) {
        return new CreateAssessmentInsightResponseDto(result.id());
    }
}
