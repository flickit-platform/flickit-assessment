package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.common.config.jwt.UserDetail;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AddEvidenceRestController {

    private final AddEvidenceUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/evidences")
    public ResponseEntity<AddEvidenceResponseDto> addEvidence(@RequestBody AddEvidenceRequestDto requestDto) {
        UserDetail user = userContext.getUser();
        log.info("User [{}] send request to add evidence", user.email());
        AddEvidenceUseCase.Result result = useCase.addEvidence(toParam(requestDto));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private Param toParam(AddEvidenceRequestDto requestDto) {
        return new AddEvidenceUseCase.Param(
            requestDto.description(),
            requestDto.createdById(),
            requestDto.assessmentId(),
            requestDto.questionId()
        );
    }

    private AddEvidenceResponseDto toResponseDto(AddEvidenceUseCase.Result result) {
        return new AddEvidenceResponseDto(result.id());
    }
}
