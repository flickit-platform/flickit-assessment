package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAnswerListRestController {

    private final GetAnswerListUseCase useCase;

    @GetMapping("{assessmentId}/answers")
    GetAnswerListResponseDto getAnswerList(
        @PathVariable("assessmentId")UUID assessmentId,
        @RequestParam("questionnaireId")Long questionnaireId){

        return toResponseDto(useCase.getAnswerList(toParam(assessmentId, questionnaireId)));
    }

    private GetAnswerListResponseDto toResponseDto(GetAnswerListUseCase.Result result) {
        return new GetAnswerListResponseDto(result.answers());
    }

    private GetAnswerListUseCase.Param toParam(UUID assessmentId, Long questionnaireId) {
        return new GetAnswerListUseCase.Param(assessmentId, questionnaireId);
    }
}
