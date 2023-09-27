package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerIsNotApplicableUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubmitAnswerIsNotApplicableRestController {

    private final SubmitAnswerIsNotApplicableUseCase useCase;

    @PutMapping("/assessment-results/{assessmentResultId}/answer-is-not-applicable")
    public ResponseEntity<SubmitAnswerIsNotApplicableResponseDto> submitAnswerIsNotApplicable(@RequestBody SubmitAnswerIsNotApplicableRequestDto requestDto,
                                                                                              @PathVariable("assessmentResultId") UUID assessmentResultId) {
        var result = toResponseDto(useCase.submitAnswerIsNotApplicable(toParam(requestDto, assessmentResultId)));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private Param toParam(SubmitAnswerIsNotApplicableRequestDto requestDto, UUID assessmentResultId) {
        return new Param(
            assessmentResultId,
            requestDto.questionnaireId(),
            requestDto.questionId(),
            requestDto.isNotApplicable());
    }

    private SubmitAnswerIsNotApplicableResponseDto toResponseDto(Result result) {
        return new SubmitAnswerIsNotApplicableResponseDto(result.id());
    }
}
