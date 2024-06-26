package org.flickit.assessment.users.application.service.tenant;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.in.tenant.GetTenantLogoUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTenantLogoService implements GetTenantLogoUseCase {

    private final AppSpecProperties appSpecProperties;

    @Override
    public Result getTenantLogo() {
        return new Result(appSpecProperties.getLogo(), appSpecProperties.getFavIcon());
    }
}
