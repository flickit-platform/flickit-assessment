package org.flickit.assessment.users.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.space.GetSpaceTypesUseCase;
import org.flickit.assessment.users.application.port.in.space.GetSpaceTypesUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetSpaceTypesRestController {

    private final GetSpaceTypesUseCase useCase;

    @GetMapping("/space-types")
    public ResponseEntity<Result> getSpaceTypes() {
        return new ResponseEntity<>(useCase.getSpaceTypes(), HttpStatus.OK);
    }
}
