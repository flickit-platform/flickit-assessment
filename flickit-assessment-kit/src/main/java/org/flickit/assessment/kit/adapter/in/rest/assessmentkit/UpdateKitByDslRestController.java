package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.adapter.in.rest.exception.ValidationExceptionHandler;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.flickit.assessment.kit.common.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateKitByDslRestController {

    private final UpdateKitByDslUseCase useCase;
    private final ValidationExceptionHandler handler;

    @PutMapping("assessment-kits/{kitId}/update-by-dsl")
    public Object updateKit(@PathVariable("kitId") Long kitId,
                                        @RequestBody UpdateKitByDslRequestDto request) {
        try {
            useCase.update(toParam(kitId, request));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ValidationException ex){
            return handler.handle(ex);
        }
    }

    private UpdateKitByDslUseCase.Param toParam(Long kitId, UpdateKitByDslRequestDto request) {
        return new UpdateKitByDslUseCase.Param(kitId, request.dslContent());
    }
}
