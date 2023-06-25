package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceToQuestionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class AddEvidenceToQuestionRestController {

    private final AddEvidenceToQuestionUseCase useCase;

    @PostMapping
    @RequestMapping("/{spaceId}/{assessmentId}/{questionId}/add-evidence")
    public ResponseEntity<AddEvidenceToQuestionResponseDto> addEvidenceToQuestion(@PathVariable("assessmentId") UUID assessmentId,
                                                                                  @PathVariable("questionId") Long questionId,
                                                                                  @RequestBody AddEvidenceToQuestionRequestDto request) {
        AddEvidenceToQuestionUseCase.Result result = useCase.addEvidenceToQuestion(toParam(request, assessmentId, questionId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private AddEvidenceToQuestionResponseDto toResponseDto(AddEvidenceToQuestionUseCase.Result result) {
        return new AddEvidenceToQuestionResponseDto(
            result.evidence()
        );
    }

    private AddEvidenceToQuestionUseCase.Param toParam(AddEvidenceToQuestionRequestDto request, UUID assessmentId, Long questionId) {
        return new AddEvidenceToQuestionUseCase.Param(
            request.description(),
            request.createdById(),
            assessmentId,
            questionId
        );
    }
}
