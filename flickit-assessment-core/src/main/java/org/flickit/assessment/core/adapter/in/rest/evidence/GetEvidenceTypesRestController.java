package org.flickit.assessment.core.adapter.in.rest.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceTypesUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetEvidenceTypesRestController {

    private final GetEvidenceTypesUseCase useCase;

    @GetMapping("/evidence-types")
    public ResponseEntity<GetEvidenceTypesResponseDto> getEvidenceTypes() {
        var response = useCase.getEvidenceTypes();
        GetEvidenceTypesResponseDto responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetEvidenceTypesResponseDto toResponseDto(GetEvidenceTypesUseCase.Result response) {
        return new GetEvidenceTypesResponseDto(response.types());
    }
}
