package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnsweredQuestionsCountUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAnsweredQuestionsCountRestController {

    private final GetAnsweredQuestionsCountUseCase useCase;

    @GetMapping("assessments/{assessmentId}/progress")
    public ResponseEntity<GetAnsweredQuestionsCountResponseDto> getAnsweredQuestionsCount(@PathVariable("assessmentId") UUID assessmentId) {
        var response = toResponse(useCase.getAnsweredQuestionsCount(toParam(assessmentId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAnsweredQuestionsCountUseCase.Param toParam(UUID assessmentId) {
        return new GetAnsweredQuestionsCountUseCase.Param(assessmentId);
    }

    private GetAnsweredQuestionsCountResponseDto toResponse(GetAnsweredQuestionsCountUseCase.Result result) {
        return new GetAnsweredQuestionsCountResponseDto(result.assessmentProgress());
    }
}
