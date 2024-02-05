package org.flickit.assessment.kit.application.port.out.question;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateQuestionPort {

    void update(Param param);

    record Param(Long id,
                 String title,
                 Integer index,
                 String hint,
                 Boolean mayNotBeApplicable,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
