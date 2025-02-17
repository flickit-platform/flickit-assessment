package org.flickit.assessment.kit.application.port.in.assessmentkit;

import java.util.List;

public interface GetKitLanguagesUseCase {

    Result getKitLanguages();

    record Result(List<Language> languages) {

        record Language(String code, String title) {
        }
    }
}
