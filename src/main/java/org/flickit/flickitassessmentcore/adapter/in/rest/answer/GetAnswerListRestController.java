package org.flickit.flickitassessmentcore.adapter.in.rest.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAnswerListRestController {

    private final GetAnswerListUseCase useCase;

    @GetMapping("{assessmentId}/answers")
    GetAnswerListResponseDto getAnswerList(
        @PathVariable("assessmentId")UUID assessmentId,
        @RequestParam("questionIds")List<Long> questionIds){

        return toResponseDto(useCase.getAnswerList(toParam(assessmentId, questionIds)));
    }

    private GetAnswerListResponseDto toResponseDto(GetAnswerListUseCase.Result result) {
        return new GetAnswerListResponseDto(result.answers());
    }

    private GetAnswerListUseCase.Param toParam(UUID assessmentId, List<Long> questionIds) {
        return new GetAnswerListUseCase.Param(assessmentId, questionIds);
    }
}
