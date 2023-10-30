package org.flickit.assessment.core.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.answer.SubmitAnswerUseCase;
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

    @PutMapping("/assessments/{assessmentId}/answer-question")
    public ResponseEntity<SubmitAnswerResponseDto> submitAnswer(@PathVariable("assessmentId") UUID assessmentId,
                                                                @RequestBody SubmitAnswerRequestDto requestDto) {
        SubmitAnswerResponseDto responseDto = toResponseDto(useCase.submitAnswer(toParam(assessmentId, requestDto)));
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    private SubmitAnswerUseCase.Param toParam(UUID assessmentId, SubmitAnswerRequestDto requestDto) {
        return new SubmitAnswerUseCase.Param(
            assessmentId,
            requestDto.questionnaireId(),
            requestDto.questionId(),
            requestDto.answerOptionId(),
            requestDto.isNotApplicable()
        );
    }

    private SubmitAnswerResponseDto toResponseDto(SubmitAnswerUseCase.Result result) {
        return new SubmitAnswerResponseDto(result.id());
    }
}
