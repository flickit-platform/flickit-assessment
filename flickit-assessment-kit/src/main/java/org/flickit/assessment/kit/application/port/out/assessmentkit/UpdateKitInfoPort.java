package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.kit.application.domain.KitMetadata;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface UpdateKitInfoPort {

    void update(Param param);

    record Param(Long kitId,
                 String code,
                 String title,
                 String summary,
                 KitLanguage lang,
                 Boolean published,
                 Boolean isPrivate,
                 Double price,
                 String about,
                 Set<Long> tags,
                 Map<KitLanguage, KitTranslation> translations,
                 boolean isRemoveTranslations,
                 KitMetadata metadata,
                 boolean isRemoveMetadata,
                 UUID currentUserId,
                 LocalDateTime lastModificationTime) {
    }
}
