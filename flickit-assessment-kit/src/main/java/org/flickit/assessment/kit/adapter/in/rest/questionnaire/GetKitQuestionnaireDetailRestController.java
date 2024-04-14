package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireDetailUseCase;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitQuestionnaireDetailRestController {

    private final GetKitQuestionnaireDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/details/questionnaires/{questionnaireId}")
    public ResponseEntity<GetKitQuestionnaireDetailResponseDto> getKitQuestionnaireDetail(@PathVariable("kitId") Long kitId,
                                                                                          @PathVariable("questionnaireId") Long questionnaireId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitQuestionnaireDetail(toParam(kitId, questionnaireId, currentUserId));
        GetKitQuestionnaireDetailResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetKitQuestionnaireDetailResponseDto toResponseDto(Result result) {
        return new GetKitQuestionnaireDetailResponseDto(result.questionsCount(),
            result.relatedSubjects(),
            result.description(),
            result.questions());
    }

    private GetKitQuestionnaireDetailUseCase.Param toParam(Long kitId, Long questionnaireId, UUID currentUserId) {
        return new GetKitQuestionnaireDetailUseCase.Param(kitId, questionnaireId, currentUserId);
    }
}
