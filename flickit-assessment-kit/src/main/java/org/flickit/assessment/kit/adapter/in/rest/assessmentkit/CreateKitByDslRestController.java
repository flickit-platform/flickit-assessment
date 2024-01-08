package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateKitByDslUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateKitByDslRestController {

    private final CreateKitByDslUseCase useCase;

    @PostMapping("/assessment-kits/create-by-dsl")
    public ResponseEntity<Void> create(@RequestBody CreateKitByDslRequestDto request) {
        useCase.create(toParam(request));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private CreateKitByDslUseCase.Param toParam(CreateKitByDslRequestDto request) {
        return new CreateKitByDslUseCase.Param(request.kitJsonDslId(),
            request.title(),
            request.summary(),
            request.about(),
            request.tags());
    }
}
