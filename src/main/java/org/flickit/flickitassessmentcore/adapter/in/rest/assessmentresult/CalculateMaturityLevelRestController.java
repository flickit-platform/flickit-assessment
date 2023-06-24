package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentresult;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("{spaceId}/{assessmentId}/assessment-result")
public class CalculateMaturityLevelRestController {

    private final CalculateMaturityLevelUseCase useCase;


    @PostMapping
    public ResponseEntity<CalculateMaturityLevelResponseDto> calculateMaturityLevel(
        @PathParam("assessmentId") UUID assessmentId) {
        CalculateMaturityLevelUseCase.Param result = new CalculateMaturityLevelUseCase.Param(assessmentId);
        CalculateMaturityLevelResponseDto responseDto =
            mapCommandToResponseDto(
                useCase.calculateMaturityLevel(result));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private CalculateMaturityLevelResponseDto mapCommandToResponseDto(CalculateMaturityLevelUseCase.Result result) {
        return new CalculateMaturityLevelResponseDto(
            result.assessmentResult()
        );
    }

}
