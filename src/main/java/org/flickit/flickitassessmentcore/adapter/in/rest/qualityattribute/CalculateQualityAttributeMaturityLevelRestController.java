package org.flickit.flickitassessmentcore.adapter.in.rest.qualityattribute;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.qualityattribute.CalculateQAMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.in.qualityattribute.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("{kitId}/{subId}/{qaId}/maturity-level")
public class CalculateQualityAttributeMaturityLevelRestController {

    private final CalculateQualityAttributeMaturityLevelUseCase useCase;


    @PostMapping
    public ResponseEntity<CalculateQAMaturityLevelResponseDto> calculateQAMaturityLevel(
        @PathParam("qaId") Long qaId,
        @RequestBody CalculateQAMaturityLevelRequestDto requestDto) {
        CalculateQAMaturityLevelCommand command = mapRequestDtoToCommand(requestDto, qaId);
        CalculateQAMaturityLevelResponseDto responseDto =
            mapCommandToResponseDto(
                useCase.calculateQualityAttributeMaturityLevel(command));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private CalculateQAMaturityLevelResponseDto mapCommandToResponseDto(MaturityLevel calculateQualityAttributeMaturityLevel) {
        return new CalculateQAMaturityLevelResponseDto(
            calculateQualityAttributeMaturityLevel
        );
    }

    private CalculateQAMaturityLevelCommand mapRequestDtoToCommand(CalculateQAMaturityLevelRequestDto requestDto, Long qaId) {
        return new CalculateQAMaturityLevelCommand(
            qaId,
            requestDto.resultId()
        );
    }
}
