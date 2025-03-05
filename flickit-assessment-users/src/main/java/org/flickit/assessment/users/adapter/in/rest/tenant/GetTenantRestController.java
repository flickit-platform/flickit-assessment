package org.flickit.assessment.users.adapter.in.rest.tenant;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.tenant.GetTenantUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetTenantRestController {

    private final GetTenantUseCase useCase;

    @GetMapping("/tenant")
    public ResponseEntity<GetTenantUseCase.Result> getTenant() {
        var result = useCase.getTenant();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
