package org.flickit.assessment.users.application.service.tenant;

import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GetTenantServiceTest {

    private GetTenantService service;
    private final AppSpecProperties appSpecProperties = new AppSpecProperties();
    private final AppAiProperties appAiProperties = new AppAiProperties();

    @BeforeEach
    void setUp() {
        appSpecProperties.setDefaultLocaleProp(new AppSpecProperties.LocaleProps("FLICKIT"));
        appSpecProperties.setLocaleProps(Map.of(
            "en", new AppSpecProperties.LocaleProps("Flickit"),
            "fa", new AppSpecProperties.LocaleProps("فلیکیت")
        ));
        appSpecProperties.setLogo("logo.png");
        appSpecProperties.setFavIcon("favicon.ico");

        appAiProperties.setEnabled(true);

        service = new GetTenantService(appSpecProperties, appAiProperties);
    }

    @Test
    void testGetTenant_whenLocaleIsEnglish_thenReturnEnglishLocaleProps() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var result = service.getTenant();

        assertEquals(appSpecProperties.getLocaleProps().get("en").getAppName(), result.name());
        assertEquals(appSpecProperties.getLogo(), result.logo().logoLink());
        assertEquals(appSpecProperties.getFavIcon(), result.logo().favLink());
        assertEquals(appAiProperties.isEnabled(), result.aiEnabled());
    }

    @Test
    void testGetTenant_whenLocaleIsPersian_thenReturnPersianLocaleProps() {
        LocaleContextHolder.setLocale(Locale.of("fa"));

        var result = service.getTenant();

        assertEquals(appSpecProperties.getLocaleProps().get("fa").getAppName(), result.name());
        assertEquals(appSpecProperties.getLogo(), result.logo().logoLink());
        assertEquals(appSpecProperties.getFavIcon(), result.logo().favLink());
        assertEquals(appAiProperties.isEnabled(), result.aiEnabled());
    }

    @Test
    void testGetTenant_whenLocaleIsNotFound_thenReturnDefaultLocaleProps() {
        LocaleContextHolder.setLocale(Locale.CHINA); // Not found in props

        var result = service.getTenant();

        assertEquals(appSpecProperties.getDefaultLocaleProp().getAppName(), result.name());
        assertEquals(appSpecProperties.getLogo(), result.logo().logoLink());
        assertEquals(appSpecProperties.getFavIcon(), result.logo().favLink());
        assertEquals(appAiProperties.isEnabled(), result.aiEnabled());
    }
}

