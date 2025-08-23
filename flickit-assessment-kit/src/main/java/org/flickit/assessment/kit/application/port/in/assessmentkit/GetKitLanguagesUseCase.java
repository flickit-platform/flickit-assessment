package org.flickit.assessment.kit.application.port.in.assessmentkit;

import java.util.List;

public interface GetKitLanguagesUseCase {

    Result getKitLanguages();

    record Result(List<KitLanguage> kitLanguages) {

        public record KitLanguage(String code, String title) {
        }
    }
}
