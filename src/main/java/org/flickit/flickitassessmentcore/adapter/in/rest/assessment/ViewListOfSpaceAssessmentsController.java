package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ViewListOfSpaceAssessmentsController {

    private final ViewListOfSpaceAssessmentsUseCase useCase;

    @GetMapping
    @RequestMapping("/{spaceId}/assessments")
    public ResponseEntity<ViewListOfSpaceAssessmentsResponseDto> viewListOfSpaceAssessments(@PathVariable("spaceId") Long spaceId) {
        ViewListOfSpaceAssessmentsRequestDto request = new ViewListOfSpaceAssessmentsRequestDto(spaceId);
        ViewListOfSpaceAssessmentsUseCase.Result result = useCase.viewListOfSpaceAssessments(toParam(request));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private ViewListOfSpaceAssessmentsResponseDto toResponseDto(ViewListOfSpaceAssessmentsUseCase.Result result) {
        return new ViewListOfSpaceAssessmentsResponseDto(result.assessments());
    }

    private ViewListOfSpaceAssessmentsUseCase.Param toParam(ViewListOfSpaceAssessmentsRequestDto request) {
        return new ViewListOfSpaceAssessmentsUseCase.Param(request.spaceId());
    }
}
