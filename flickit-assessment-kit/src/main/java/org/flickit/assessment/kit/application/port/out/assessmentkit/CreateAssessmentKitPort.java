package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.UUID;

public interface CreateAssessmentKitPort {

    Long persist(Param param);

    record Param(String code,
                 String title,
                 String summary,
                 String about,
                 KitLanguage lang,
                 boolean published,
                 boolean isPrivate,
                 long expertGroupId,
                 UUID createdBy) {
    }
}
