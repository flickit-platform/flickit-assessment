package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ViewListOfSpaceAssessmentsRestController {

    private final GetAssessmentListUseCase useCase;

    @GetMapping
    @RequestMapping("/get-assessments")
    public ResponseEntity<ViewListOfSpaceAssessmentsResponseDto> viewListOfSpaceAssessments(
        @RequestBody ViewListOfSpaceAssessmentsRequestDto request) {
        GetAssessmentListUseCase.Result result = useCase.viewListOfSpaceAssessments(toParam(request));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private GetAssessmentListUseCase.Param toParam(ViewListOfSpaceAssessmentsRequestDto request) {
        return new GetAssessmentListUseCase.Param(request.spaceId());
    }

    private ViewListOfSpaceAssessmentsResponseDto toResponseDto(GetAssessmentListUseCase.Result result) {
        return new ViewListOfSpaceAssessmentsResponseDto(result.assessments());
    }
}
