package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateQuestionnaireRestController {

    private final CreateQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/{kitId}/questionnaires")
    public ResponseEntity<CreateQuestionnaireResponseDto> createQuestionnaire(@PathVariable("kitId") Long kitId,
                                                                              @RequestBody CreateQuestionnaireRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        long questionnaireId = useCase.createQuestionnaire(toParam(kitId, requestDto, currentUserId));
        return new ResponseEntity<>(new CreateQuestionnaireResponseDto(questionnaireId), HttpStatus.CREATED);
    }

    private CreateQuestionnaireUseCase.Param toParam(Long kitId,
                                                     CreateQuestionnaireRequestDto requestDto,
                                                     UUID currentUserId) {
        return new CreateQuestionnaireUseCase.Param(kitId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            currentUserId);
    }
}