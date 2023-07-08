package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AddEvidenceRestController {

    private final AddEvidenceUseCase useCase;

    @PostMapping("/evidences")
    public ResponseEntity<AddEvidenceResponseDto> addEvidence(@RequestBody AddEvidenceRequestDto requestDto) {
        AddEvidenceUseCase.Result result = useCase.addEvidence(toParam(requestDto));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    public Param toParam(AddEvidenceRequestDto requestDto) {
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
