package org.flickit.assessment.users.adapter.in.rest.tenant;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.tenant.GetTenantLogoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetTenantLogoRestController {

    private final GetTenantLogoUseCase useCase;

    @GetMapping("tenant/logo")
    public ResponseEntity<GetTenantLogoUseCase.Result> getTenantLogo(){
        var result = useCase.getTenantLogo();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
