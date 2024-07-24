package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase.Param;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.flickit.assessment.core.adapter.in.rest.evidence.GetEvidenceResponseDto.EvidenceDto;
import static org.flickit.assessment.core.adapter.in.rest.evidence.GetEvidenceResponseDto.QuestionDto;

@RestController
@RequiredArgsConstructor
public class GetEvidenceRestController {

    private final GetEvidenceUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/evidences/{id}")
    public ResponseEntity<GetEvidenceResponseDto> getEvidence(@PathVariable("id") UUID id) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.getEvidence(toParam(id, currentUserId));
        var response = toResponse(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(UUID id, UUID currentUserId) {
        return new Param(id, currentUserId);
    }

    private GetEvidenceResponseDto toResponse(Result result) {
        return new GetEvidenceResponseDto(
            EvidenceDto.of(result.evidence(), result.user()),
            QuestionDto.of(result.question(), result.answer())
        );
    }
}
