package org.flickit.assessment.kit.adapter.in.rest.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.measure.GetKitMeasureDetailUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitMeasureDetailRestController {

    private final GetKitMeasureDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/details/measures/{measureId}")
    public ResponseEntity<GetKitMeasureDetailUseCase.Result> getKitMeasureDetail(@PathVariable("kitId") Long kitId,
                                                                                 @PathVariable("measureId") Long measureId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitMeasureDetail(toParam(kitId, measureId, currentUserId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetKitMeasureDetailUseCase.Param toParam(Long kitId, Long questionnaireId, UUID currentUserId) {
        return new GetKitMeasureDetailUseCase.Param(kitId, questionnaireId, currentUserId);
    }
}
