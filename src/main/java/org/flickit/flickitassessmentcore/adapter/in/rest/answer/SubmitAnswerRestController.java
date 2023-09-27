package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubmitAnswerRestController {


    private final SubmitAnswerUseCase useCase;

    @PutMapping("/assessment-results/{assessmentResultId}/answer-question")
    public ResponseEntity<SubmitAnswerResponseDto> submitAnswer(@RequestBody SubmitAnswerRequestDto requestDto,
                                                                @PathVariable("assessmentResultId") UUID assessmentResultId) {
        SubmitAnswerResponseDto responseDto = toResponseDto(useCase.submitAnswer(toParam(requestDto, assessmentResultId)));
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    private SubmitAnswerUseCase.Param toParam(SubmitAnswerRequestDto requestDto, UUID assessmentResultId) {
        return new SubmitAnswerUseCase.Param(
            assessmentResultId,
            requestDto.questionnaireId(),
            requestDto.questionId(),
            requestDto.answerOptionId()
        );
    }

    private SubmitAnswerResponseDto toResponseDto(SubmitAnswerUseCase.Result result) {
        return new SubmitAnswerResponseDto(result.id());
    }
}
