package org.flickit.assessment.kit.adapter.in.rest.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.measure.CreateMeasureUseCase;
import org.flickit.assessment.kit.application.port.in.measure.CreateMeasureUseCase.Param;
import org.flickit.assessment.kit.application.port.in.measure.CreateMeasureUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateMeasureRestController {

    private final CreateMeasureUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/measures")
    public ResponseEntity<Result> createMeasure(@PathVariable("kitVersionId") Long kitVersionId,
                                                @RequestBody CreateMeasureRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.createMeasure(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId,
                          CreateMeasureRequestDto requestDto,
                          UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.index(),
            requestDto.title(),
            requestDto.description(),
            requestDto.translations(),
            currentUserId);
    }
}
