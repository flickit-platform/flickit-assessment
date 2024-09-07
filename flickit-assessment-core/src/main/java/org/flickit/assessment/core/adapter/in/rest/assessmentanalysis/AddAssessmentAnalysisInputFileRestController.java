package org.flickit.assessment.core.adapter.in.rest.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.AddAssessmentAnalysisInputFileUseCase;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.AddAssessmentAnalysisInputFileUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.AddAssessmentAnalysisInputFileUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddAssessmentAnalysisInputFileRestController {

    private final AddAssessmentAnalysisInputFileUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessments/{assessmentId}/analysis-input")
    public ResponseEntity<Result> addAssessmentAnalysisInputFile(
        @PathVariable UUID assessmentId,
        @ModelAttribute AddAssessmentAnalysisInputFileRequestDto requestDto) {

        UUID currentUserId = userContext.getUser().id();
        Result result =
            useCase.addAssessmentAnalysisInputFile(toParam(assessmentId, currentUserId, requestDto));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId, AddAssessmentAnalysisInputFileRequestDto requestDto) {
        return new Param(assessmentId,
            requestDto.inputFile(),
            requestDto.analysisType(),
            currentUserId);
    }
}
