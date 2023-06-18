package org.flickit.flickitassessmentcore.adapter.in.rest.assessmentresult;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        CalculateMaturityLevelCommand command = new CalculateMaturityLevelCommand(assessmentId);
        CalculateMaturityLevelResponseDto responseDto =
            mapCommandToResponseDto(
                useCase.calculateMaturityLevel(command));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private CalculateMaturityLevelResponseDto mapCommandToResponseDto(AssessmentResult assessmentResult) {
        return new CalculateMaturityLevelResponseDto(
            assessmentResult
        );
    }

}
