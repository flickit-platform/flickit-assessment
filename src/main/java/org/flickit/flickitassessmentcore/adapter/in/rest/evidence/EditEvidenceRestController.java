package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.EditEvidenceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class EditEvidenceRestController {

    private final EditEvidenceUseCase useCase;

    @PutMapping("/evidences")
    public ResponseEntity<EditEvidenceResponseDto> editEvidence(
        @RequestParam UUID id,
        @RequestBody EditEvidenceRequestDto request) {
        EditEvidenceUseCase.Result result = useCase.editEvidence(toParam(id, request));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private EditEvidenceUseCase.Param toParam(UUID id, EditEvidenceRequestDto request) {
        return new EditEvidenceUseCase.Param(
            id,
            request.description()
        );
    }

    private EditEvidenceResponseDto toResponse(EditEvidenceUseCase.Result result) {
        return new EditEvidenceResponseDto(result.id());
    }
}
