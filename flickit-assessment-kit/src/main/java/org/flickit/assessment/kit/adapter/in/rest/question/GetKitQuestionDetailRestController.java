package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitQuestionDetailRestController {

    private final GetKitQuestionDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}/details/questions/{questionId}")
    public ResponseEntity<GetKitQuestionDetailResponseDto> getKitQuestionDetail(@PathVariable("kitId") Long kitId,
                                                                                @PathVariable("questionId") Long questionId) {
        var currentUserId = userContext.getUser().id();
        var response = useCase.getKitQuestionDetail(toParam(kitId, questionId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private Param toParam(Long kitId, Long subjectId, UUID currentUserId) {
        return new Param(kitId, subjectId, currentUserId);
    }

    private GetKitQuestionDetailResponseDto toResponseDto(Result result) {
        return new GetKitQuestionDetailResponseDto(result.hint(), result.options(), result.attributeImpacts());
    }
}
