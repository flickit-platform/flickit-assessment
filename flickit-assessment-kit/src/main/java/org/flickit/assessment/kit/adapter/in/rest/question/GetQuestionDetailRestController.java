package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionDetailRestController {

    private final GetQuestionDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}/details/questions/{questionId}")
    public ResponseEntity<GetQuestionDetailResponseDto> getQuestionDetail(@PathVariable("kitId") Long kitId,
                                                                          @PathVariable("questionId") Long questionId) {
        var currentUserId = userContext.getUser().id();
        var response = useCase.getQuestionDetail(toParam(kitId, questionId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private Param toParam(Long kitId, Long subjectId, UUID currentUserId) {
        return new Param(kitId, subjectId, currentUserId);
    }

    private GetQuestionDetailResponseDto toResponseDto(Result result) {
        return new GetQuestionDetailResponseDto(result.options(), result.attributeImpacts());
    }
}
