package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsUseCase;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ViewListOfSpaceAssessmentsController {

    private final ViewListOfSpaceAssessmentsUseCase useCase;

    @GetMapping
    @RequestMapping("/{spaceId}/assessments")
    public ResponseEntity<ViewListOfSpaceAssessmentsResponseDto> viewListOfSpaceAssessments(@PathVariable("spaceId") Long spaceId) {
        ViewListOfSpaceAssessmentsRequestDto request = new ViewListOfSpaceAssessmentsRequestDto(spaceId);
        List<Assessment> assessments = useCase.viewListOfSpaceAssessments(toCommand(request));
        return new ResponseEntity<>(toResponseDto(assessments), HttpStatus.OK);
    }

    private ViewListOfSpaceAssessmentsResponseDto toResponseDto(List<Assessment> assessments) {
        return new ViewListOfSpaceAssessmentsResponseDto(assessments);
    }

    private ViewListOfSpaceAssessmentsCommand toCommand(ViewListOfSpaceAssessmentsRequestDto request) {
        return new ViewListOfSpaceAssessmentsCommand(request.spaceId());
    }
}
