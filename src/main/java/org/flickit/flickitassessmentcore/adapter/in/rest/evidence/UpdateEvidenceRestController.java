package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.UpdateEvidenceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateEvidenceRestController {

    private final UpdateEvidenceUseCase useCase;

    @PutMapping("/evidences")
    public ResponseEntity<UpdateEvidenceResponseDto> updateEvidence(
        @RequestParam UUID id,
        @RequestBody UpdateEvidenceRequestDto request) {
        UpdateEvidenceUseCase.Result result = useCase.updateEvidence(toParam(id, request));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private UpdateEvidenceUseCase.Param toParam(UUID id, UpdateEvidenceRequestDto request) {
        return new UpdateEvidenceUseCase.Param(id, request.description());
    }

    private UpdateEvidenceResponseDto toResponse(UpdateEvidenceUseCase.Result result) {
        return new UpdateEvidenceResponseDto(result.id());
    }
}
