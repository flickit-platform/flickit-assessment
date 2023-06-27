package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_LIST_SPACE_ID_EQUAL_OR_GREATER_THAN_ZERO;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_LIST_SPACE_ID_NOT_NULL;

@RequiredArgsConstructor
@RestController
@Validated
public class GetAssessmentListRestController {

    private final GetAssessmentListUseCase useCase;

    @GetMapping("/assessments")
    public ResponseEntity<GetAssessmentListResponseDto> getAssessmentList(
        @RequestParam("spaceId")
        @NotNull(message = GET_ASSESSMENT_LIST_SPACE_ID_NOT_NULL)
        @Min(value = 0, message = GET_ASSESSMENT_LIST_SPACE_ID_EQUAL_OR_GREATER_THAN_ZERO)
        Long spaceId,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {
        GetAssessmentListUseCase.Result result = useCase.getAssessmentList(toParam(spaceId, size, page));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private GetAssessmentListUseCase.Param toParam(Long spaceId, int size, int page) {
        return new GetAssessmentListUseCase.Param(spaceId, size, page);
    }

    private GetAssessmentListResponseDto toResponseDto(GetAssessmentListUseCase.Result result) {
        return new GetAssessmentListResponseDto(result.assessments());
    }
}
