package org.flickit.assessment.kit.adapter.in.rest.questionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateQuestionImpactRestController {

    private final CreateQuestionImpactUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/question-impacts")
    public ResponseEntity<CreateQuestionImpactResponseDto> createQuestionImpact(@PathVariable("kitVersionId") Long kitVersionId,
                                                                                @RequestBody CreateQuestionImpactRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        var questionImpactId = useCase.createQuestionImpact(toParam(kitVersionId, requestDto, currentUserId));

        return new ResponseEntity<>(new CreateQuestionImpactResponseDto(questionImpactId), HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId, CreateQuestionImpactRequestDto requestDto, UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.attributeId(),
            requestDto.maturityLevelId(),
            requestDto.questionId(),
            requestDto.weight(),
            currentUserId);
    }
}
