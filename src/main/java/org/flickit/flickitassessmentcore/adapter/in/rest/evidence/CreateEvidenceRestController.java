package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.CreateEvidenceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.ADD_EVIDENCE_QUESTION_ID_NOT_NULL;

@RequiredArgsConstructor
@RestController
public class CreateEvidenceRestController {

    private final CreateEvidenceUseCase useCase;

    @PostMapping("/evidences")
    public ResponseEntity<CreateEvidenceResponseDto> createEvidence(
        @RequestParam("assessmentId")
        @NotNull(message = ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId,
        @RequestParam("questionId")
        @NotNull(message = ADD_EVIDENCE_QUESTION_ID_NOT_NULL)
        Long questionId,
        @RequestBody CreateEvidenceRequestDto request) {
        CreateEvidenceUseCase.Result result = useCase.createEvidence(toParam(request, assessmentId, questionId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private CreateEvidenceUseCase.Param toParam(CreateEvidenceRequestDto request, UUID assessmentId, Long questionId) {
        return new CreateEvidenceUseCase.Param(
            request.description(),
            request.createdById(),
            assessmentId,
            questionId
        );
    }

    private CreateEvidenceResponseDto toResponseDto(CreateEvidenceUseCase.Result result) {
        return new CreateEvidenceResponseDto(
            result.evidence()
        );
    }
}
