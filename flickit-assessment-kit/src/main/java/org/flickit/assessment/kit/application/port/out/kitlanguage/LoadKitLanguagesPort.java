package org.flickit.assessment.kit.application.port.out.kitlanguage;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.List;

public interface LoadKitLanguagesPort {

    List<Result> loadByKitIds(List<Long> kitIds);

    record Result(long kitId, List<KitLanguage> kitLanguages) {
    }
}
