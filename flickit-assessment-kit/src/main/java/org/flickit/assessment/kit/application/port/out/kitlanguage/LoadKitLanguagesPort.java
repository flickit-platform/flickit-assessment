package org.flickit.assessment.kit.application.port.out.kitlanguage;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.List;
import java.util.Map;

public interface LoadKitLanguagesPort {

    Map<Long, List<KitLanguage>> loadByKitIds(List<Long> kitIds);

    List<KitLanguage> loadByKitId(long kitId);
}
