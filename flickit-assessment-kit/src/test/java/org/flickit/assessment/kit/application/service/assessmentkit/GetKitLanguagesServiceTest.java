package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitLanguagesUseCase.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetKitLanguagesServiceTest {

    private final GetKitLanguagesService service = new GetKitLanguagesService();

    @Test
    void testGetKitLanguages() {
        List<Result.KitLanguage> kitLanguages = Arrays.stream(KitLanguage.values())
            .map(e -> new Result.KitLanguage(e.getCode(), e.getTitle()))
            .toList();

        assertEquals(new Result(kitLanguages), service.getKitLanguages());
    }
}
