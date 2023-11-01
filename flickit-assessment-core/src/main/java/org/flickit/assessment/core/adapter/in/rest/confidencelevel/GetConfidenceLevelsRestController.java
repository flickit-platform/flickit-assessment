package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelsUseCase;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetConfidenceLevelsRestController {

    private final GetConfidenceLevelsUseCase useCase;

    @GetMapping("/confidence-levels")
    public ResponseEntity<GetConfidenceLevelsResponseDto> getConfidenceLevels() {
        var response = useCase.getConfidenceLevels();
        var responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetConfidenceLevelsResponseDto toResponseDto(Result response) {
        return new GetConfidenceLevelsResponseDto(response.defaultConfidenceLevel(), response.confidenceLevels());
    }

}
