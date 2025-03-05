package org.flickit.assessment.users.application.service.tenant;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.in.tenant.GetTenantUseCase;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTenantService implements GetTenantUseCase {

    private final AppSpecProperties appSpecProperties;
    private final AppAiProperties appAiProperties;

    @Override
    public Result getTenant() {
        var locale = LocaleContextHolder.getLocale().getLanguage();
        var localeProps = appSpecProperties.getLocaleProps()
            .getOrDefault(locale, appSpecProperties.getDefaultLocaleProp());
        return new Result(
            localeProps.getAppName(),
            new GetTenantUseCase.Logo(appSpecProperties.getLogo(), appSpecProperties.getFavIcon()),
            appAiProperties.isEnabled()
        );
    }
}
