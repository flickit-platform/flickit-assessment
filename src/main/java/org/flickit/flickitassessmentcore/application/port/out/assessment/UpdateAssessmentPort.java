package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentPort {

    Result update(Param param);

    record Param(UUID id, String title, Integer colorId, LocalDateTime lastModificationTime) {}

    record Result(UUID id) {}
}
