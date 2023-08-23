package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentPort {

    Result update(Param param);

    record Param(String title, Long colorId, LocalDateTime lastUpdateTime) {}

    record Result(UUID id) {}
}
