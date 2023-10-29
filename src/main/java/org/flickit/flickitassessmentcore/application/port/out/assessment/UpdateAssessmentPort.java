package org.flickit.flickitassessmentcore.application.port.out.assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentPort {

    Result update(AllParam param);

    void updateLastModificationTime(UUID id, LocalDateTime lastModificationTime);

    record AllParam(UUID id,
                    String title,
                    String code,
                    Integer colorId,
                    LocalDateTime lastModificationTime) {}

    record Result(UUID id) {}
}
