package org.flickit.assessment.kit.application.port.out.subject;

import java.util.UUID;

public interface CreateSubjectPort {

    Long persist(Param param);

    record Param(
        String code,
        String title,
        int index,
        int weight,
        String description,
        Long kitVersionId,
        UUID createdBy) {}
}
