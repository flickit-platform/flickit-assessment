package org.flickit.flickitassessmentcore.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.evidence.EvidenceMapper;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
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
    public ResponseEntity<AddEvidenceResponseDto> addEvidence(@RequestBody AddEvidenceRequestDto request) {
        AddEvidenceUseCase.Result result = useCase.addEvidence(EvidenceMapper.toAddEvidenceUseCaseParam(request));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.CREATED);
    }

    private AddEvidenceResponseDto toResponseDto(AddEvidenceUseCase.Result result) {
        return new AddEvidenceResponseDto(
            result.id()
        );
    }
}
