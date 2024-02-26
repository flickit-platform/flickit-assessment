package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AddEvidenceRestController {

    private final AddEvidenceUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/evidences")
    public ResponseEntity<AddEvidenceResponseDto> addEvidence(@RequestBody AddEvidenceRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        AddEvidenceUseCase.Result result = useCase.addEvidence(toParam(requestDto, currentUserId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private Param toParam(AddEvidenceRequestDto requestDto, UUID currentUserId) {
        return new AddEvidenceUseCase.Param(
            requestDto.description(),
            requestDto.assessmentId(),
            requestDto.questionId(),
            requestDto.type(),
            currentUserId
        );
    }

    private AddEvidenceResponseDto toResponseDto(AddEvidenceUseCase.Result result) {
        return new AddEvidenceResponseDto(result.id());
    }
}
