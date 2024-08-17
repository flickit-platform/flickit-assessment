package org.flickit.assessment.users.adapter.in.rest.tenant;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.tenant.GetTenantInfoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetTenantInfoRestController {

    private final GetTenantInfoUseCase useCase;

    @GetMapping("tenant/info")
    public ResponseEntity<GetTenantInfoUseCase.Result> getTenantInfo(){
        var result = useCase.getTenantInfo();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
