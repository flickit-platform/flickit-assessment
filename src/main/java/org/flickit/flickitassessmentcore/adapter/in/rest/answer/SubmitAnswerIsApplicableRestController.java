package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsApplicableUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsApplicableUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class SubmitAnswerIsApplicableRestController {

    private final SubmitAnswerIsApplicableUseCase useCase;

    @PutMapping("/assessment-results/{assessmentResultId}/answer-question/is-applicable")
    public ResponseEntity<SubmitAnswerIsApplicableResponseDto> submitApplicableAnswer(@RequestBody SubmitAnswerIsApplicableRequestDto requestDto,
                                                                                      @PathVariable("assessmentResultId") UUID assessmentResultId) {
        SubmitAnswerIsApplicableResponseDto responseDto = toResponseDto(useCase.submitAnswerIsApplicable(toParam(requestDto, assessmentResultId)));
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    private Param toParam(SubmitAnswerIsApplicableRequestDto requestDto, UUID assessmentResultId) {
        return new Param(
            assessmentResultId,
            requestDto.questionId(),
            requestDto.isApplicable());
    }

    private SubmitAnswerIsApplicableResponseDto toResponseDto(Result result) {
        return new SubmitAnswerIsApplicableResponseDto(result.id());
    }
}
