package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.util.UUID;

public interface CreateAssessmentKitPort {

    Result persist(Param param);

    record Param(String code,
                 String title,
                 String summary,
                 String about,
                 boolean published,
                 boolean isPrivate,
                 long expertGroupId,
                 UUID createdBy) {
    }

    record Result(Long kitId, Long kitVersionId) {}
}
