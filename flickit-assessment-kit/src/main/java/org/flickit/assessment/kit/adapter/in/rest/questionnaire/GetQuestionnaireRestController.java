package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetQuestionnaireRestController {

    private final GetQuestionnaireUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/details/questionnaires/{questionnaireId}")
    public ResponseEntity<GetQuestionnaireResponseDto> getQuestionnaire(@PathVariable("kitId") Long kitId,
                                                                        @PathVariable("questionnaireId") Long questionnaireId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getQuestionnaire(toParam(kitId, questionnaireId, currentUserId));
        GetQuestionnaireResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetQuestionnaireResponseDto toResponseDto(Result result) {
        return new GetQuestionnaireResponseDto(result.questionsCount(),
            result.relatedSubjects(),
            result.description(),
            result.questions());
    }

    private GetQuestionnaireUseCase.Param toParam(Long kitId, Long questionnaireId, UUID currentUserId) {
        return new GetQuestionnaireUseCase.Param(kitId, questionnaireId, currentUserId);
    }
}
