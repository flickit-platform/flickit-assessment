package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.util.SpringUtil;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitLanguagesUseCase.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GetKitLanguagesServiceTest {

    @InjectMocks
    private GetKitLanguagesService service;

    @Mock
    ApplicationContext applicationContext;

    @BeforeEach
    void prepare() {
        var props = new AppSpecProperties();
        doReturn(props).when(applicationContext).getBean(AppSpecProperties.class);
        new SpringUtil(applicationContext);
    }

    @Test
    void testGetKitLanguages() {
        List<Result.KitLanguage> expectedLangs = Arrays.stream(KitLanguage.values())
            .map(e -> new Result.KitLanguage(e.getCode(), e.getTitle()))
            .toList();

        var result = service.getKitLanguages();

        assertThat(result.kitLanguages())
            .containsExactlyInAnyOrderElementsOf(expectedLangs);
    }
}
