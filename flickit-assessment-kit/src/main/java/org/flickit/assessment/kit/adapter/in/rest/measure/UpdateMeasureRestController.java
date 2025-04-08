package org.flickit.assessment.kit.adapter.in.rest.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.measure.UpdateMeasureUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateMeasureRestController {

    private final UpdateMeasureUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/measures/{measureId}")
    public ResponseEntity<Void> updateMeasure(@PathVariable("kitVersionId") Long kitVersionId,
                                              @PathVariable("measureId") Long measureId,
                                              @RequestBody UpdateMeasureRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateMeasure(toParam(kitVersionId, measureId, currentUserId, requestDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateMeasureUseCase.Param toParam(Long kitVersionId,
                                               Long measureId,
                                               UUID currentUserId,
                                               UpdateMeasureRequestDto requestDto) {
        return new UpdateMeasureUseCase.Param(kitVersionId,
            measureId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            currentUserId);
    }
}
