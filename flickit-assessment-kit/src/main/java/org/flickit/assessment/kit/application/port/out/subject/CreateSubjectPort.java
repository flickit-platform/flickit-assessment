package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;

import java.util.Map;
import java.util.UUID;

public interface CreateSubjectPort {

    Long persist(Param param);

    record Param(
        String code,
        String title,
        int index,
        int weight,
        Map<KitLanguage, SubjectTranslation> translations,
        String description,
        Long kitVersionId,
        UUID createdBy) {
    }
}
