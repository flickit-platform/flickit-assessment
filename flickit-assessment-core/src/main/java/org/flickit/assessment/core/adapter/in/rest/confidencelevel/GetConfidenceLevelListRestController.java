package org.flickit.assessment.core.adapter.in.rest.confidencelevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelListUseCase;
import org.flickit.assessment.core.application.port.in.confidencelevel.GetConfidenceLevelListUseCase.ConfidenceLevelItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetConfidenceLevelListRestController {

    private final GetConfidenceLevelListUseCase useCase;

    @GetMapping("/confidence-levels")
    public ResponseEntity<GetConfidenceLevelListResponseDto> getConfidenceLevels() {
        var response = useCase.getConfidenceLevels();
        var responseDto = toResponseDto(response);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private GetConfidenceLevelListResponseDto toResponseDto(List<ConfidenceLevelItem> confidenceLevels) {
        return new GetConfidenceLevelListResponseDto(confidenceLevels);
    }

}
