package org.flickit.assessment.kit.adapter.in.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.question.CreateQuestionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateQuestionRestController {

    private final CreateQuestionUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/{kitId}/questionnaires/{questionnaireId}/questions")
    public ResponseEntity<CreateQuestionResponseDto> createQuestion(@PathVariable("kitId") Long kitId,
                                                                    @PathVariable("questionnaireId") Long questionnaireId,
                                                                    @RequestBody CreateQuestionRequestDto dto) {
        UUID currentUserId = userContext.getUser().id();
        long questionId = useCase.createQuestion(toParam(kitId, questionnaireId, currentUserId, dto));
        return new ResponseEntity<>(new CreateQuestionResponseDto(questionId), HttpStatus.CREATED);
    }

    private static CreateQuestionUseCase.Param toParam(Long kitId,
                                                       Long questionnaireId,
                                                       UUID currentUserId,
                                                       CreateQuestionRequestDto dto) {
        return new CreateQuestionUseCase.Param(kitId,
            dto.index(),
            dto.title(),
            dto.hint(),
            dto.mayNotBeApplicable(),
            dto.advisable(),
            questionnaireId,
            currentUserId);
    }
}
