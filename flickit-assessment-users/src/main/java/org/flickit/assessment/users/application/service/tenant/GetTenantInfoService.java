package org.flickit.assessment.users.application.service.tenant;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.in.tenant.GetTenantInfoUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTenantInfoService implements GetTenantInfoUseCase {

    private final AppSpecProperties appSpecProperties;

    @Override
    public Result getTenantInfo() {
        return new Result(appSpecProperties.getName());
    }
}
